package dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.axis.fintech.dto.LoginRequest;

public class LoginRequestTest {

	@Test
	void testConstructorSetsFields() {
		LoginRequest request = new LoginRequest("testuser", "testpass");

		assertThat(request.getUserName()).isEqualTo("testuser");
		assertThat(request.getPassword()).isEqualTo("testpass");
	}

	@Test
	void testSetUserName() {
		LoginRequest request = new LoginRequest("initial", "password");
		request.setUserName("updated");

		assertThat(request.getUserName()).isEqualTo("updated");
	}

	@Test
	void testSetPassword() {
		LoginRequest request = new LoginRequest("user", "initialPass");
		request.setPassword("newPass");

		assertThat(request.getPassword()).isEqualTo("newPass");
	}
}
