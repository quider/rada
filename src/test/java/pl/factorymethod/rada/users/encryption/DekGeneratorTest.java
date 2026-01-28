package pl.factorymethod.rada.users.encryption;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DekGeneratorTest {

    private DekGenerator dekGenerator;

    @BeforeEach
    void setUp() {
        dekGenerator = new DekGenerator(32);
    }

    @Test
    void generatesCorrectSizeDek() {
        byte[] dek = dekGenerator.generateDek();
        
        assertThat(dek).hasSize(32);
    }

    @Test
    void generatesUniqueDeks() {
        byte[] dek1 = dekGenerator.generateDek();
        byte[] dek2 = dekGenerator.generateDek();
        
        assertThat(dek1).isNotEqualTo(dek2);
    }

    @Test
    void generatesNonZeroDek() {
        byte[] dek = dekGenerator.generateDek();
        
        boolean hasNonZero = false;
        for (byte b : dek) {
            if (b != 0) {
                hasNonZero = true;
                break;
            }
        }
        
        assertThat(hasNonZero).isTrue();
    }

    @Test
    void supportsCustomDekSize() {
        DekGenerator customGenerator = new DekGenerator(16);
        byte[] dek = customGenerator.generateDek();
        
        assertThat(dek).hasSize(16);
    }
}
