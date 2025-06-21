package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.axis.fintech.utils.HashUtil;

class HashUtilTest {

    @Test
    void testSha256_returnsExpectedHash() {
        String input = "password123";
        String expected = "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f"; // Precomputed SHA-256 hash

        String actual = HashUtil.sha256(input);

        assertEquals(expected, actual);
    }

    @Test
    void testSha256_emptyString() {
        String input = "";
        String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // SHA-256 of ""

        String actual = HashUtil.sha256(input);

        assertEquals(expected, actual);
    }

    @Test
    void testSha256_nullInput_throwsException() {
        assertThrows(NullPointerException.class, () -> HashUtil.sha256(null));
    }
}
