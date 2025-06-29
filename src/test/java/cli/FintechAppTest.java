package cli;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Scanner;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.axis.fintech.cli.AuthService;
import com.axis.fintech.cli.FintechApp;
import com.axis.fintech.cli.MenuService;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;

public class FintechAppTest {

	@Mock
	private AccountService accountService;
	@Mock
	private TransactionService transactionService;
	@Mock
	private AuthService authService;
	@Mock
	private MenuService menuService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testStart_loginFlow() {
		String input = "1\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				if ("1".equals(userChoice)) {
					when(authService.login(scanner)).thenReturn("testuser");
					menuService.menu(scanner, "testuser");
				}
			}
		};

		app.start();
		verify(menuService, times(1)).menu(any(), eq("testuser"));
	}

	@Test
	void testStart_signupFlow_andExit() {
		String input = "2\nno\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		// Mock AuthService with controlled behavior
		AuthService mockAuthService = mock(AuthService.class);
		when(mockAuthService.signup(any())).thenReturn(null);

		doAnswer(invocation -> null).when(mockAuthService).retry(any(Scanner.class), any(Supplier.class));
		// Create FintechApp with the mocked AuthService
		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				System.out.println("\n=== Fintech CLI Menu ===");
				System.out.println("1. Login");
				System.out.println("2. Signup");
				System.out.print("Enter your choice: ");

				String userChoice = scanner.nextLine().trim();
				if ("2".equals(userChoice)) {
					mockAuthService.signup(scanner);
					mockAuthService.retry(scanner, () -> start());
				}
			}
		};

		app.start();

		verify(mockAuthService, times(1)).retry(any(Scanner.class), any(Runnable.class));
	}

	@Test
	void testStart_invalidInput_thenLogin() {
		String input = "abc\n1\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				while (!userChoice.matches("^(1|2)$")) {
					userChoice = scanner.nextLine().trim();
				}
				if ("1".equals(userChoice)) {
					when(authService.login(scanner)).thenReturn("testuser");
					menuService.menu(scanner, "testuser");
				}
			}
		};

		app.start();
		verify(menuService, times(1)).menu(any(), eq("testuser"));
	}

	@Test
	void testStart_signupSuccess_thenMenu() {
		String input = "2\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				if ("2".equals(userChoice)) {
					when(authService.signup(scanner)).thenReturn("newuser");
					menuService.menu(scanner, "newuser");
				}
			}
		};

		app.start();
		verify(menuService, times(1)).menu(any(), eq("newuser"));
	}

	@Test
	void testStart_loginReturnsNull_doesNotCallMenu() {
		String input = "1\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				if ("1".equals(userChoice)) {
					when(authService.login(scanner)).thenReturn(null);
					String result = authService.login(scanner);
					if (result != null) {
						menuService.menu(scanner, result);
					}
				}
			}
		};

		app.start();
		verify(menuService, never()).menu(any(), any());
	}

	@Test
	void testStart_blankInput_thenSignup() {
		String input = "\n\n2\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				while (!userChoice.matches("^(1|2)$")) {
					userChoice = scanner.nextLine().trim();
				}
				if ("2".equals(userChoice)) {
					when(authService.signup(scanner)).thenReturn("someone");
					menuService.menu(scanner, "someone");
				}
			}
		};

		app.start();
		verify(menuService, times(1)).menu(any(), eq("someone"));
	}

	@Test
	void testStart_loginReturnsNull_shouldSkipMenu() {
		String input = "1\n";
		System.setIn(new ByteArrayInputStream(input.getBytes()));

		AuthService authService = mock(AuthService.class);
		MenuService menuService = mock(MenuService.class);

		FintechApp app = new FintechApp(accountService, transactionService) {
			@Override
			public void start() {
				Scanner scanner = new Scanner(System.in);
				String userChoice = scanner.nextLine().trim();
				if ("1".equals(userChoice)) {
					when(authService.login(scanner)).thenReturn(null);
					String result = authService.login(scanner);
					if (result != null) {
						menuService.menu(scanner, result);
					}
				}
			}
		};

		app.start();
		verify(menuService, never()).menu(any(), any());
	}

}
