package dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.axis.fintech.dto.UserChoiceRequest;

public class UserChoiceRequestTest {

    @Test
    void testSetAndGetChoice() {
        UserChoiceRequest request = new UserChoiceRequest();
        request.setChoice(3);

        assertEquals(3, request.getChoice());
    }

    @Test
    void testDefaultChoiceValue() {
        UserChoiceRequest request = new UserChoiceRequest();

        // Since the field is a primitive int, its default value is 0
        assertEquals(0, request.getChoice());
    }
}
