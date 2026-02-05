package pl.factorymethod.rada.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.factorymethod.rada.auth.dto.LoginResponse;
import pl.factorymethod.rada.auth.repository.UserRepository;
import pl.factorymethod.rada.model.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    /**
     * Provision user in Rada database based on JWT token information. If user already exists,
     * it will be updated with latest information from token. 
     * This method is calledduring authentication process to ensure that user information
     * in Rada is always up-to-date with Keycloak.
     * @param authentication
     * @return
     */
    public LoginResponse provisionUser(Authentication authentication) {
        String userId = extractUserIdFromJwt(authentication);
        String email = extractEmailFromJwt(authentication);
        String name = extractNameFromJwt(authentication);

        log.info("Provisioning user: userId={}, email={}, name={}", userId, email, name);
        UUID publicId = UUID.fromString(userId);
        if (!userRepository.existsByPublicId(publicId)) {
            createUser(userId, email, name);
        }
        return new LoginResponse(userId, email, name);
    }

    private void createUser(String userId, String email, String name) {
        User user = new User();
        user.setPublicId(UUID.fromString(userId));
        user.setEmail(email);
        user.setName(name);
        user.setEnabled(true);
        user.setExpired(false);
        user.setDeleted(false);
        SecureRandom secureRandom = new SecureRandom(Instant.now().toString().getBytes());
        byte[] dek = new byte[32];
        secureRandom.nextBytes(dek);
        user.setDek(dek);
        userRepository.save(user);
        log.info("Created new user: {}", email);
    }

    private String extractUserIdFromJwt(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return authentication.getPrincipal().toString();
    }

    private String extractEmailFromJwt(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    private String extractNameFromJwt(Authentication authentication) {
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("name");
        }
        return null;
    }

}
