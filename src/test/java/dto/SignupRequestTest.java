package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.axis.fintech.dto.SignupRequest;

public class SignupRequestTest {

    @Test
    void testConstructorAndGetters() {
        SignupRequest request = new SignupRequest("mariam", "secure123");

        assertEquals("mariam", request.getUserName());
        assertEquals("secure123", request.getPassword());
    }

    @Test
    void testSetters() {
        SignupRequest request = new SignupRequest("olduser", "oldpass");

        request.setUserName("newuser");
        request.setPassword("newpass");

        assertEquals("newuser", request.getUserName());
        assertEquals("newpass", request.getPassword());
    }
}
