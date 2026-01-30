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
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.User;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.users.dto.CreateUserRequest;
import pl.factorymethod.rada.users.dto.CreateClassUserRequest;
import pl.factorymethod.rada.users.dto.CreateClassUsersRequest;
import pl.factorymethod.rada.users.dto.UserResponse;
import pl.factorymethod.rada.users.encryption.DekGenerator;
import pl.factorymethod.rada.users.event.UserCreatedEvent;
import pl.factorymethod.rada.shared.events.DomainEventPublisher;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final DekGenerator dekGenerator;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check if user already exists
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        });

        // Find student
        UUID studentPublicId = UUID.fromString(request.getStudentId());
        Student student = studentRepository.findByPublicId(studentPublicId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        // Generate DEK (Data Encryption Key) for this user
        byte[] dek = dekGenerator.generateDek();
        
        // Create user
        User user = new User();
        user.setPublicId(UUID.randomUUID());
        user.setStudent(student);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // TODO: Hash password in future
        user.setEnabled(true);
        user.setExpired(false);
        user.setDeleted(false);
        user.setDek(dek);

        user = userRepository.save(user);

        log.info("User created: publicId={}, email={}, DEK generated (length={})",
                user.getPublicId(), user.getEmail(), dek.length);

        // Publish domain event
        eventPublisher.publish(new UserCreatedEvent(
                user.getPublicId(),
                user.getEmail(),
                LocalDateTime.now()));

        return new UserResponse(
                user.getPublicId().toString(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.isEnabled());
    }

    private String createJoinCode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createJoinCode'");
}

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
        for (CreateClassUserRequest userRequest : users) {
            Student student = studentsByPublicId.get(UUID.fromString(userRequest.getStudentId()));
            byte[] dek = dekGenerator.generateDek();

            User user = new User();
            user.setPublicId(UUID.randomUUID());
            user.setStudent(student);
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPhone(userRequest.getPhone());
            user.setPassword(userRequest.getPassword());
            user.setEnabled(true);
            user.setExpired(false);
            user.setDeleted(false);
            user.setJoinCode(createJoinCode());
            user.setDek(dek);

            usersToSave.add(user);
        }

        List<User> savedUsers = userRepository.saveAll(usersToSave);
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
}
