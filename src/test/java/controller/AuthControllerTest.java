package controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.axis.fintech.FintechApplication;
import com.axis.fintech.model.Account;
import com.axis.fintech.security.JwtTokenProvider;
import com.axis.fintech.service.AccountService;

@SpringBootTest(classes = FintechApplication.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@MockBean
	private AuthenticationManager authenticationManager;

	@MockBean
	private JwtTokenProvider jwtTokenProvider;

	@MockBean
	private AccountService accountService;

	@BeforeEach
	void setUp() {
		Account testAccount = new Account("testuser", 1L, 0.0, passwordEncoder.encode("password123"));
		when(accountService.findByUserName("testuser")).thenReturn(testAccount);
		when(accountService.validatePassword("testuser", "password123")).thenReturn(true);
		when(jwtTokenProvider.generateToken(any())).thenReturn("mocked-jwt");
	}

	@Test
	void testLoginSuccess() throws Exception {
		String json = """
				    {
				      "userName": "testuser",
				      "password": "password123"
				    }
				""";
		mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk())
				.andExpect(header().string("Authorization", "Bearer mocked-jwt"))
				.andExpect(content().string("Login successful"));
	}

	@Test
	void testLoginInvalidUsername() throws Exception {
		String json = """
				    {
				      "userName": "unknownuser",
				      "password": "password123"
				    }
				""";

		when(accountService.findByUserName("unknownuser")).thenReturn(null);

		mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(json))
				.andExpect(status().isNotFound()).andExpect(content().string("User not found: unknownuser"));
	}

	@Test
	void testLoginIncorrectPassword() throws Exception {
		String json = """
				    {
				      "userName": "testuser",
				      "password": "wrongpassword"
				    }
				""";

		Account testAccount = new Account("testuser", 1L, 0.0, passwordEncoder.encode("password123"));
		when(accountService.findByUserName("testuser")).thenReturn(testAccount);
		when(accountService.validatePassword("testuser", "wrongpassword")).thenReturn(false);

		mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON).content(json))
				.andExpect(status().isUnauthorized()).andExpect(content().string("Invalid password"));
	}

	@Test
	void testSignupSuccess() throws Exception {
		String json = """
				    {
				      "userName": "newuser",
				      "password": "newpassword"
				    }
				""";

		// Mock that the user does not already exist
		when(accountService.findByUserName("newuser")).thenReturn(null);
		when(accountService.openAccount("newuser", "newpassword")).thenReturn(100L);

		mockMvc.perform(post("/api/auth/signup").contentType(APPLICATION_JSON).content(json)).andExpect(status().isOk())
				.andExpect(content().string("Account created successfully. Your account ID is: 100"));
	}

	@Test
	void testSignupUserAlreadyExists() throws Exception {
		String json = """
				    {
				      "userName": "existinguser",
				      "password": "somepass"
				    }
				""";

		// Mock that the user already exists
		Account existingAccount = new Account("existinguser", 1L, 0.0, passwordEncoder.encode("somepass"));
		when(accountService.findByUserName("existinguser")).thenReturn(existingAccount);

		mockMvc.perform(post("/api/auth/signup").contentType(APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andExpect(content().string("Username already exists"));
	}

	@Test
	void testSignupInvalidUsernameFormat() throws Exception {
		String json = """
				    {
				      "userName": "123_invalid_user",
				      "password": "securePassword"
				    }
				""";

		mockMvc.perform(post("/api/auth/signup").contentType(APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest()).andExpect(content().string("Invalid username format"));
	}

}
