package cli;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.axis.fintech.cli.MenuService;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    AccountService accountService;

    @Mock
    TransactionService transactionService;

    private MenuService menuService;

    @BeforeEach
    void setUp() {
        // Prevent actual System.exit in tests
        menuService = new MenuService(accountService, transactionService, status-> System.exit(0));
    }

    @Test
    void testChoice1_deposit_validAmount_thenExit() {
        String input = "1\n100.0\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(transactionService.deposit("user1", 100.0)).thenReturn(123L);
        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
        	menuService.menu(scanner, "user1")
        );

       


        verify(transactionService).deposit("user1", 100.0);
    }

    @Test
    void testChoice1_deposit_invalidAmount_thenExit() {
        String input = "1\nabd.0\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
            menuService.menu(scanner, "user1")
        );

        assertEquals("Exit intercepted", e.getMessage());
        assertTrue(exitCalled[0]);
        verify(transactionService, never()).deposit(anyString(), anyDouble());
    }


    @Test
    void testChoice2_withdraw_validAmount_thenExit() {
        String input = "2\n200.0\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(transactionService.withdraw("user1", 200.0)).thenReturn(321L);

        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
            menuService.menu(scanner, "user1")
        );

        verify(transactionService).withdraw("user1", 200.0);
    }

    @Test
    void testChoice2_withdraw_invalidAmount_thenExit() {
        String input = "2\nwrong\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
            menuService.menu(scanner, "user1")
        );

        assertEquals("Exit intercepted", e.getMessage());
        assertTrue(exitCalled[0]);
        verify(transactionService, never()).withdraw(anyString(), anyDouble());
    }


    @Test
    void testChoice3_checkBalance_thenExit() {
        String input = "3\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(accountService.getBalance("user1")).thenReturn(555.5);

        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
        	menuService.menu(scanner, "user1")
        );

        assertEquals("Exit intercepted", e.getMessage());
        assertTrue(exitCalled[0]);
        verify(accountService).getBalance("user1");
    }

    @Test
    void testChoice4_exitDirectly() {
        String input = "4\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
        	menuService.menu(scanner, "user1")
        );
        assertEquals("Exit intercepted", e.getMessage());
        // No side effects to verify, test exits cleanly
    }

    @Test
    void testInvalidChoice_nonNumber() {
        String input = "abc\n4\n"; // 'abc' is invalid, then retry with valid
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        final boolean[] exitCalled = {false};
        MenuService menuService = new MenuService(accountService, transactionService, status -> {
            exitCalled[0] = true;
            throw new RuntimeException("Exit intercepted");
        });

        RuntimeException e = assertThrows(RuntimeException.class, () -> 
        	menuService.menu(scanner, "user1")
        );

        assertEquals("Exit intercepted", e.getMessage());
        // You can capture output if needed, or just ensure it doesn't crash

        // No assert here unless you're validating System.out
        // At least assert it didn't crash
        assertDoesNotThrow(() -> menuService.menu(scanner, "user1"));
    }

    @Test
    void testInvalidChoice_numberOutOfRange() {
        String input = "9\nno\n"; // '9' is invalid -> triggers exit confirmation
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        final boolean[] exitCalled = {false};

        MenuService menuService = new MenuService(accountService, transactionService, status -> exitCalled[0] = true);

        menuService.menu(scanner, "user1");

        assertTrue(exitCalled[0], "Expected exit to be called after invalid input and choosing 'no'");
    }




    @Test
    void testToContinue_invalidInput_thenYes() {
        String input = "1\n100\nmaybe\ny\n4\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(transactionService.deposit("user1", 100.0)).thenReturn(1000L);

        final boolean[] exited = { false };
        menuService = new MenuService(accountService, transactionService, status -> exited[0] = true);

        menuService.menu(scanner, "user1");

        verify(transactionService).deposit("user1", 100.0);
        assertTrue(exited[0]);
    }
    @Test
    void testToContinue_multipleInvalids_thenNo() {
        String input = "1\n100\nmaybe\nwhat\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        when(transactionService.deposit("user1", 100.0)).thenReturn(42L);

        final boolean[] exited = {false};
        menuService = new MenuService(accountService, transactionService,  status -> exited[0] = true);

        menuService.menu(scanner, "user1");

        verify(transactionService).deposit("user1", 100.0);
        assertTrue(exited[0]);
    }
    @Test
    void testFullSession_depositThenWithdrawThenExit() {
        String input = "1\n50\ny\n2\n30\nno\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        when(transactionService.deposit("user1", 50.0)).thenReturn(10L);
        when(transactionService.withdraw("user1", 30.0)).thenReturn(20L);

        final boolean[] exited = {false};
        menuService = new MenuService(accountService, transactionService,  status -> exited[0] = true);

        menuService.menu(scanner, "user1");

        verify(transactionService).deposit("user1", 50.0);
        verify(transactionService).withdraw("user1", 30.0);
        assertTrue(exited[0]);
    }



}
