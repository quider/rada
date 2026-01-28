package pl.factorymethod.rada.users.encryption;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Generator for Data Encryption Keys (DEK).
 * Each user gets a unique DEK that will be used to encrypt their sensitive data.
 */
@Slf4j
@Component
public class DekGenerator {

    private final SecureRandom secureRandom;
    private final int dekSizeBytes;

    public DekGenerator(@Value("${rada.encryption.dek-size-bytes:32}") int dekSizeBytes) {
        this.secureRandom = new SecureRandom();
        this.dekSizeBytes = dekSizeBytes;
        log.info("DekGenerator initialized with key size: {} bytes", dekSizeBytes);
    }

    /**
     * Generate a cryptographically secure random DEK.
     * Default size is 32 bytes (256 bits) suitable for AES-256.
     *
     * @return byte array containing the DEK
     */
    public byte[] generateDek() {
        byte[] dek = new byte[dekSizeBytes];
        secureRandom.nextBytes(dek);
        log.debug("Generated new DEK of {} bytes", dekSizeBytes);
        return dek;
    }
}
