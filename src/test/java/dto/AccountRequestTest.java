package dto;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.axis.fintech.dto.AccountRequest;

public class AccountRequestTest {

    @Test
    void testAllArgsConstructor() {
        AccountRequest request = new AccountRequest("testuser", "testpass");

        assertThat(request.getUserName()).isEqualTo("testuser");
        assertThat(request.getPassword()).isEqualTo("testpass");
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        AccountRequest request = new AccountRequest();
        request.setUserName("admin");
        request.setPassword("admin123");

        assertThat(request.getUserName()).isEqualTo("admin");
        assertThat(request.getPassword()).isEqualTo("admin123");
    }

    @Test
    void testDefaultValuesAfterNoArgConstructor() {
        AccountRequest request = new AccountRequest();

        assertThat(request.getUserName()).isNull();
        assertThat(request.getPassword()).isNull();
    }

    @Test
    void testSettersUpdateValues() {
        AccountRequest request = new AccountRequest("initial", "init123");
        request.setUserName("updatedUser");
        request.setPassword("newPass");

        assertThat(request.getUserName()).isEqualTo("updatedUser");
        assertThat(request.getPassword()).isEqualTo("newPass");
    }
}
