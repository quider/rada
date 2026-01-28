package pl.factorymethod.rada.users;

import java.time.LocalDateTime;
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
}
