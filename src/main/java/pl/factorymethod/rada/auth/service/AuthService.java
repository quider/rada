package pl.factorymethod.rada.auth.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${keycloak.userinfo-uri}")
    private String userinfoUri;

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

    /**
     * Call Keycloak userinfo endpoint to get additional user details (e.g.
     * join_code)
     * 
     * @return
     */
    private String callUserinfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            String token = jwt.getTokenValue();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            try {
                ResponseEntity<String> response = restTemplate.exchange(userinfoUri, HttpMethod.GET, request,
                        String.class);
                log.info("Userinfo response: {}", response.getBody());

                Map<String, Object> userInfo = objectMapper.readValue(response.getBody(),
                        new TypeReference<Map<String, Object>>() {
                        });
                return (String) userInfo.get("join_code");
            } catch (Exception e) {
                log.error("Failed to fetch userinfo from {}", userinfoUri, e);
            }
        }
        return null;
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
        userRepository.saveJoinCode(user.getId(), callUserinfo());
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
