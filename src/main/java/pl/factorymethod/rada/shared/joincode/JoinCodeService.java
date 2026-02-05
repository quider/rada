package pl.factorymethod.rada.shared.joincode;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pl.factorymethod.rada.auth.repository.StudentJoinCodeRepository;

@Service
@RequiredArgsConstructor
public class JoinCodeService {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 8;
    private static final int MAX_ATTEMPTS = 10;

    private final StudentJoinCodeRepository studentJoinCodeRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            String code = generateCode();
            if (!studentJoinCodeRepository.existsByJoinCode(code)) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique join code");
    }

    private String generateCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }
        return builder.toString();
    }
}
