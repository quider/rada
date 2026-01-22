package pl.factorymethod.rada.auth.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.auth.dto.LoginRequest;
import pl.factorymethod.rada.auth.dto.LoginResponse;
import pl.factorymethod.rada.auth.repository.UserRepository;
import pl.factorymethod.rada.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        // User user = userRepository.findByEmail(request.getEmail())
                // .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        User user = new User();
        user.setEmail("a@a.a");
        user.setPassword("password");
        user.setEnabled(true);
        user.setDeleted(false);
        user.setExpired(false);
        user.setId(1L);
        user.setPublicId(UUID.fromString("7b6a52b7-445f-4917-91a1-43dd49d3e2d2"));
        
        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }
        
        if (user.isDeleted()) {
            throw new RuntimeException("Account is deleted");
        }
        
        if (user.isExpired()) {
            throw new RuntimeException("Account is expired");
        }
        
        // TODO: Add password encryption comparison (e.g., BCrypt)
        // if (!user.getPassword().equals(request.getPassword())) {
        //     throw new RuntimeException("Invalid email or password");
        // }
        
        // TODO: Generate proper JWT token
        String token = generateToken(user);
        
        log.info("User {} logged in successfully", user.getId());
        return new LoginResponse(token, user.getPublicId());
    }
    
    private String generateToken(User user) {
        // Temporary token generation - replace with JWT
        return UUID.randomUUID().toString();
    }

    public LoginResponse checkSession() {
        User user = new User();
        user.setEmail("a@a.a");
        user.setPassword("password");
        user.setEnabled(true);
        user.setDeleted(false);
        user.setExpired(false);
        user.setId(1L);
        user.setPublicId(UUID.fromString("7b6a52b7-445f-4917-91a1-43dd49d3e2d2"));
        // TODO Auto-generated method stub
       return new LoginResponse(generateToken(user), user.getPublicId());
    }
}
