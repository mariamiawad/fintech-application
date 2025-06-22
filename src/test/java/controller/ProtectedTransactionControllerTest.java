package controller;

import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.axis.fintech.FintechApplication;
import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;

@SpringBootTest(classes = FintechApplication.class)
@AutoConfigureMockMvc
public class ProtectedTransactionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@MockBean
	private TransactionService transactionService;

	@BeforeEach
	void setUp() {
		Account account = new Account("testuser", 1L, 100.0, "hashedPassword");
		when(accountService.findByUserName("testuser")).thenReturn(account);
		when(accountService.validatePassword("testuser", "password123")).thenReturn(true);

	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSecuredDeposit_WithJWT() throws Exception {
		when(transactionService.deposit("testuser", 50.0)).thenReturn(101L);
		String json = """
				    {
				        "amount": 50.0
				    }
				""";

		mockMvc.perform(post("/api/secure/deposit").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Transaction successful. ID: 101"));
	}

	@Test
	@WithMockUser(username = "unknown", roles = "USER")
	void testSecuredDeposit_UserNotFound() throws Exception {
		// Simulate no account returned
		when(accountService.findByUserName("unknown")).thenReturn(null);
		String json = """
				    {
				        "amount": 50.0
				    }
				""";
		System.out.println(SecurityContextHolder.getContext().getAuthentication());

		mockMvc.perform(post("/api/secure/deposit").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isFound()) // 302 redirect
				.andExpect(header().string("Location", "/"))
				.andExpect(content().string("User not found. Redirecting to home..."));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSecuredWithdraw_Success() throws Exception {
		Account account = new Account("testuser", 1L, 100.0, "hashedPassword");
		when(accountService.findByUserName("testuser")).thenReturn(account);
		when(transactionService.withdraw("testuser", 30.0)).thenReturn(102L);

		String json = """
				    {
				        "amount": 30.0
				    }
				""";

		mockMvc.perform(post("/api/secure/withdraw").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Transaction successful. ID: 102"));
	}

	@Test
	@WithMockUser(username = "unknown", roles = "USER")
	void testSecuredWithdraw_UserNotFound() throws Exception {
		when(accountService.findByUserName("unknown")).thenReturn(null);

		String json = """
				    {
				        "amount": 30.0
				    }
				""";

		mockMvc.perform(post("/api/secure/withdraw").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isFound()).andExpect(header().string("Location", "/"))
				.andExpect(content().string("User not found. Redirecting to home..."));
	}

	@Test
	@WithMockUser(username = "testuser", roles = "USER")
	void testSecureBalance_Success() throws Exception {
		Account account = new Account("testuser", 1L, 150.0, "hashedPassword");
		when(accountService.findByUserName("testuser")).thenReturn(account);
		when(transactionService.checkBalance("testuser")).thenReturn(150.0);

		String json = "{}"; 

		mockMvc.perform(post("/api/secure/balance").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(content().string("Your balance is: 150.0"));
	}

	@Test
	@WithMockUser(username = "ghost", roles = "USER")
	void testSecureBalance_UserNotFound() throws Exception {
		when(accountService.findByUserName("ghost")).thenReturn(null);

		mockMvc.perform(post("/api/secure/balance").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isFound()).andExpect(header().string("Location", "/"))
				.andExpect(content().string("User not found. Redirecting to home..."));
	}

}
