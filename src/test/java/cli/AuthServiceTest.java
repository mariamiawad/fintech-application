package cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.axis.fintech.cli.AuthService;
import com.axis.fintech.cli.MenuService;
import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.utils.HashUtil;

class AuthServiceTest {

	@Mock
	private AccountService accountService;

	private AuthService authService;
	@Mock
	private MenuService menuService;
	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		authService = new AuthService(accountService);
	}

	@Test
	void testLogin_success() {
	    String input = "user1\npassword1\n";
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

	    Account acc = new Account();
	    acc.setUserName("user1");
	    acc.setPasswordHash(HashUtil.sha256("password1")); // optional since we stub validatePassword

	    when(accountService.findByUserName("user1")).thenReturn(acc);
	    when(accountService.validatePassword("user1", "password1")).thenReturn(true);

	    String result = authService.login(scanner);
	    assertEquals("user1", result);
	}


	@Test
	void testLogin_wrongPassword_thenCorrect() {
	    String input = "user1\n";
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

	    when(accountService.findByUserName("user1")).thenReturn(new Account());

	    when(accountService.validatePassword("user1", "wrong")).thenReturn(false);
	    when(accountService.validatePassword("user1", "right")).thenReturn(true);

	    AuthService testAuthService = new AuthService(accountService) {
	        private int attempt = 0;

	        @Override
	        public String readPassword(Scanner scanner) {
	            return (attempt++ == 0) ? "wrong" : "right";
	        }
	    };

	    String result = testAuthService.login(scanner);
	    assertEquals("user1", result);
	}







	@Test
	void testLogin_userNotFound() {
		String input = "userX\nno\n";
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		when(accountService.findByUserName("userX")).thenReturn(null);

		authService = new AuthService(accountService) {
			@Override
			public boolean retry(Scanner scanner, Runnable retryAction) {
				return false;
			}
		};

		String result = authService.login(scanner);
		assertNull(result);
	}

	@Test
	void testSignup_success() {
		String input = "newuser\nsecurepass\n";
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		when(accountService.openAccount(eq("newuser"), anyString())).thenReturn(123L);

		String result = authService.signup(scanner);
		assertEquals("newuser", result);
	}
	@Test
	void testSignup_failure_thenRetry_yes() {
	    String input = "newuser\npass\nyes\nnewuser2\npass2\n";
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

	    when(accountService.openAccount(eq("newuser"), anyString())).thenReturn(null);
	    when(accountService.openAccount(eq("newuser2"), anyString())).thenReturn(321L);

	    authService = new AuthService(accountService) {
	        @Override
	        public boolean retry(Scanner scanner, Runnable retryAction) {
	            // This will consume "yes" from the input correctly
	            System.out.print("Would you like to return to main menu? (yes/no): ");
	            String retry = scanner.nextLine().trim().toLowerCase();
	            return retry.equals("yes") || retry.equals("y");
	        }

	        @Override
	        public String readPassword(Scanner scanner) {
	            return scanner.nextLine(); // correct password handling
	        }
	    };

	    String result = authService.signup(scanner);
	    assertEquals("newuser2", result);
	}


	@Test
	void testSignup_failureThenExit() {
		String input = "newuser\npass\nno\n";
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
		when(accountService.openAccount(anyString(), anyString())).thenReturn(null);

		authService = new AuthService(accountService) {
			@Override
			public boolean retry(Scanner scanner, Runnable retryAction) {
				return false;
			}
		};

		String result = authService.signup(scanner);
		assertNull(result);
	}

	@Test
	void testRetry_yes() {
		String input = "yes\n";
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

		final boolean[] called = { false };
		authService.retry(scanner, () -> called[0] = true);

		assertTrue(called[0]);
	}

	@Test
	void testRetry_no_callsSystemExit() {
	    SecurityManager original = System.getSecurityManager();
	    System.setSecurityManager(new NoExitSecurityManager()); // intercept exit

	    String input = "no\n";
	    Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

	    try {
	        ExitException e = assertThrows(ExitException.class, () -> {
	            authService.retry(scanner, () -> fail("Should not call retryAction"));
	        });
	        assertEquals(0, e.status);
	    } finally {
	        System.setSecurityManager(original); // always restore!
	    }
	}


	@Test
	void testRetry_invalid_thenYes() {
		String input = "maybe\ny\n";
		Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

		final boolean[] called = { false };
		authService.retry(scanner, () -> called[0] = true);

		assertTrue(called[0]);
	}
	static class ExitException extends SecurityException {
	    public final int status;
	    public ExitException(int status) {
	        super("Intercepted System.exit with status: " + status);
	        this.status = status;
	    }
	}

	static class NoExitSecurityManager extends SecurityManager {
	    @Override
	    public void checkPermission(java.security.Permission perm) {
	        // allow all other operations
	    }

	    @Override
	    public void checkExit(int status) {
	        throw new ExitException(status);
	    }
	}

}
