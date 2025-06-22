package security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.axis.fintech.security.JwtTokenProvider;

public class JwtTokenProviderTest {

	private JwtTokenProvider jwtTokenProvider;

	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		jwtTokenProvider = new JwtTokenProvider();

		// Manually inject the values since @Value is not handled in plain unit tests
		injectTestValues(jwtTokenProvider);

		userDetails = new User("mariam", "password", Collections.emptyList());
	}

	@Test
	void testGenerateAndParseToken() {
		String token = jwtTokenProvider.generateToken(userDetails);
		assertNotNull(token);

		String username = jwtTokenProvider.getUsernameFromToken(token);
		assertEquals("mariam", username);
	}

	@Test
	void testValidateToken_validToken() {
		String token = jwtTokenProvider.generateToken(userDetails);
		boolean isValid = jwtTokenProvider.validateToken(token, userDetails);
		assertTrue(isValid);
	}

	@Test
	void testValidateToken_wrongUser() {
		String token = jwtTokenProvider.generateToken(userDetails);

		UserDetails anotherUser = new User("wronguser", "password", Collections.emptyList());
		boolean isValid = jwtTokenProvider.validateToken(token, anotherUser);
		assertFalse(isValid);
	}

	// Simulates @Value injection in unit test
	private void injectTestValues(JwtTokenProvider provider) {
		try {
			var secretField = JwtTokenProvider.class.getDeclaredField("jwtSecret");
			var expField = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");

			secretField.setAccessible(true);
			expField.setAccessible(true);

			secretField.set(provider, "YXNkZmFzZGZhc2RmYXNkZg=="); // base64 string
			expField.set(provider, 3600000L); // 1 hour
		} catch (Exception e) {
			throw new RuntimeException("Failed to inject @Value fields manually", e);
		}
	}
}
