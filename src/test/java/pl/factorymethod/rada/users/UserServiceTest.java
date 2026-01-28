package pl.factorymethod.rada.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import pl.factorymethod.rada.auth.repository.UserRepository;
import pl.factorymethod.rada.model.Student;
import pl.factorymethod.rada.model.User;
import pl.factorymethod.rada.shared.events.DomainEventPublisher;
import pl.factorymethod.rada.targets.repository.StudentRepository;
import pl.factorymethod.rada.users.dto.CreateUserRequest;
import pl.factorymethod.rada.users.dto.UserResponse;
import pl.factorymethod.rada.users.encryption.DekGenerator;
import pl.factorymethod.rada.users.event.UserCreatedEvent;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private DekGenerator dekGenerator;
    @Mock
    private DomainEventPublisher eventPublisher;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createsUserWithGeneratedDek() {
        CreateUserRequest request = CreateUserRequest.builder()
                .studentId(UUID.randomUUID().toString())
                .name("Jan Kowalski")
                .email("jan@example.com")
                .phone("123456789")
                .password("password123")
                .build();

        Student student = new Student();
        student.setId(1L);
        student.setPublicId(UUID.fromString(request.getStudentId()));

        byte[] dek = new byte[32];
        for (int i = 0; i < 32; i++) {
            dek[i] = (byte) i;
        }

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.findByPublicId(UUID.fromString(request.getStudentId())))
                .thenReturn(Optional.of(student));
        when(dekGenerator.generateDek()).thenReturn(dek);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(request.getEmail());
        assertThat(response.getName()).isEqualTo(request.getName());
        assertThat(response.getPhone()).isEqualTo(request.getPhone());
        assertThat(response.isEnabled()).isTrue();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getDek()).isEqualTo(dek);
        assertThat(savedUser.getDek().length).isEqualTo(32);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(UserCreatedEvent.class);

        verify(dekGenerator, times(1)).generateDek();
    }

    @Test
    void throwsWhenUserAlreadyExists() {
        CreateUserRequest request = CreateUserRequest.builder()
                .studentId(UUID.randomUUID().toString())
                .email("existing@example.com")
                .name("Test")
                .phone("123")
                .password("pass")
                .build();

        User existingUser = new User();
        existingUser.setEmail(request.getEmail());

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void throwsWhenStudentNotFound() {
        CreateUserRequest request = CreateUserRequest.builder()
                .studentId(UUID.randomUUID().toString())
                .email("new@example.com")
                .name("Test")
                .phone("123")
                .password("pass")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(studentRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Student not found");
    }
}
