package pl.factorymethod.rada.users;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.auth.repository.UserRepository;
import pl.factorymethod.rada.auth.repository.StudentJoinCodeRepository;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.StudentJoinCode;
import pl.factorymethod.rada.model.User;
import pl.factorymethod.rada.shared.joincode.JoinCodeService;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.users.dto.CreateClassUserRequest;
import pl.factorymethod.rada.users.dto.CreateClassUsersRequest;
import pl.factorymethod.rada.users.dto.UserResponse;
import pl.factorymethod.rada.users.encryption.DekGenerator;
import pl.factorymethod.rada.users.event.UserCreatedEvent;
import pl.factorymethod.rada.shared.events.EventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final StudentJoinCodeRepository studentJoinCodeRepository;
    private final DekGenerator dekGenerator;
    private final EventPublisher eventPublisher;
    private final JoinCodeService joinCodeService;

    @Transactional
    public List<UserResponse> createUsersForClass(CreateClassUsersRequest request) {
        UUID classPublicId = UUID.fromString(request.getClassId());
        List<CreateClassUserRequest> users = request.getUsers();

        List<UUID> studentIds = new ArrayList<>(users.size());
        Set<String> emails = new HashSet<>(users.size());
        Set<String> phones = new HashSet<>(users.size());
        Set<UUID> studentIdsUnique = new HashSet<>(users.size());

        for (CreateClassUserRequest userRequest : users) {
            UUID studentId = UUID.fromString(userRequest.getStudentId());
            if (!studentIdsUnique.add(studentId)) {
                throw new RuntimeException("Duplicate student ID in request: " + userRequest.getStudentId());
            }
            if (!emails.add(userRequest.getEmail())) {
                throw new RuntimeException("Duplicate email in request: " + userRequest.getEmail());
            }
            if (!phones.add(userRequest.getPhone())) {
                throw new RuntimeException("Duplicate phone in request: " + userRequest.getPhone());
            }
            studentIds.add(studentId);
        }

        List<Student> students = studentRepository.findByPublicIdIn(studentIds);
        if (students.size() != studentIds.size()) {
            throw new RuntimeException("One or more students not found");
        }

        Map<UUID, Student> studentsByPublicId = new HashMap<>(students.size());
        for (Student student : students) {
            studentsByPublicId.put(student.getPublicId(), student);
            if (student.getSchoolClass() == null || student.getSchoolClass().getPublicId() == null) {
                throw new RuntimeException("Student not assigned to class: " + student.getPublicId());
            }
            if (!student.getSchoolClass().getPublicId().equals(classPublicId)) {
                throw new RuntimeException("Student not in class: " + student.getPublicId());
            }
        }

        List<User> existingByEmail = userRepository.findByEmailIn(new ArrayList<>(emails));
        if (!existingByEmail.isEmpty()) {
            throw new RuntimeException("Users with provided emails already exist");
        }

        List<User> existingByPhone = userRepository.findByPhoneIn(new ArrayList<>(phones));
        if (!existingByPhone.isEmpty()) {
            throw new RuntimeException("Users with provided phones already exist");
        }

        List<User> usersToSave = new ArrayList<>(users.size());
        List<Student> studentsForUsers = new ArrayList<>(users.size());
        for (CreateClassUserRequest userRequest : users) {
            Student student = studentsByPublicId.get(UUID.fromString(userRequest.getStudentId()));
            byte[] dek = dekGenerator.generateDek();

            User user = new User();
            user.setPublicId(UUID.randomUUID());
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPhone(userRequest.getPhone());
            user.setPassword(userRequest.getPassword());
            user.setEnabled(true);
            user.setExpired(false);
            user.setDeleted(false);
            user.setDek(dek);

            usersToSave.add(user);
            studentsForUsers.add(student);
        }

        List<User> savedUsers = userRepository.saveAll(usersToSave);
        List<StudentJoinCode> joinCodes = new ArrayList<>(savedUsers.size());
        for (int i = 0; i < savedUsers.size(); i++) {
            joinCodes.add(createJoinCodeLink(studentsForUsers.get(i), savedUsers.get(i)));
        }
        studentJoinCodeRepository.saveAll(joinCodes);
        LocalDateTime now = LocalDateTime.now();
        for (User user : savedUsers) {
            eventPublisher.publish(new UserCreatedEvent(
                    user.getPublicId(),
                    user.getEmail(),
                    now));
        }

        List<UserResponse> responses = new ArrayList<>(savedUsers.size());
        for (User user : savedUsers) {
            responses.add(new UserResponse(
                    user.getPublicId().toString(),
                    user.getEmail(),
                    user.getName(),
                    user.getPhone(),
                    user.isEnabled()));
        }
        return responses;
    }

    @Transactional
    public void joinToClass(String userPublicId, String joinCode) {
        UUID userId = UUID.fromString(userPublicId);
        User user = userRepository.findByPublicId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userPublicId));

        int updated = studentJoinCodeRepository.assignUserToJoinCode(user.getId(), joinCode);
        if (updated == 0) {
            if (studentJoinCodeRepository.findByJoinCode(joinCode).isEmpty()) {
                throw new RuntimeException("Join code not found: " + joinCode);
            }
            throw new RuntimeException("Join code already used: " + joinCode);
        }
    }

    private StudentJoinCode createJoinCodeLink(Student student, User user) {
        StudentJoinCode joinCode = new StudentJoinCode();
        joinCode.setStudent(student);
        joinCode.setUser(user);
        joinCode.setJoinCode(joinCodeService.generateUniqueCode());
        return joinCode;
    }
}
