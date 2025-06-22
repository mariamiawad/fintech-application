package dto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.axis.fintech.dto.TransactionRequest;

public class TransactionRequestTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        TransactionRequest request = new TransactionRequest();
        request.setUserName("mariam");
        request.setAmount(200.0);

        assertEquals("mariam", request.getUserName());
        assertEquals(200.0, request.getAmount());
        assertNull(request.getPassword()); // no setter called
    }

    @Test
    void testAllArgsConstructor() {
        TransactionRequest request = new TransactionRequest("john", 500.0);

        assertEquals("john", request.getUserName());
        assertEquals(500.0, request.getAmount());
        assertNull(request.getPassword()); // not set in constructor
    }

    @Test
    void testSetPassword() {
        TransactionRequest request = new TransactionRequest();
        request.setUserName("alice");
        request.setAmount(100.0);

        assertNull(request.getPassword()); // Still null due to missing setter
    }
}
