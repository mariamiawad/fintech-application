package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.utils.HashUtil;
import com.axis.fintech.utils.JsonFileHandler;

class AccountServiceTest {

    private AccountService accountService;
    private static MockedStatic<JsonFileHandler> mockedFileHandler;

    private List<Account> accountsStub;

    @BeforeAll
    static void beforeAll() {
        mockedFileHandler = mockStatic(JsonFileHandler.class);
    }

    @AfterAll
    static void afterAll() {
        mockedFileHandler.close();
    }

    @BeforeEach
    void setUp() {
        accountsStub = new ArrayList<>();
        mockedFileHandler.when(JsonFileHandler::loadAccounts).thenReturn(accountsStub);
        accountService = new AccountService();
        accountService.init();
    }

    @Test
    void testOpenAccount_success() {
        mockedFileHandler.when(() -> JsonFileHandler.saveAccounts(any())).thenAnswer(invocation -> null);

        Long id = accountService.openAccount("user123", "pass123");
        assertNotNull(id);
        assertEquals(1, accountService.getAllAccounts().size());
    }

    @Test
    void testOpenAccount_existingUser_returnsNull() {
        Account existing = new Account("user123", 1000L, 50.0, HashUtil.sha256("pass123"));
        accountsStub.add(existing);

        Long id = accountService.openAccount("user123", "newpass");
        assertNull(id);
    }

    @Test
    void testOpenAccount_invalidUsername_logsError() {
        mockedFileHandler.when(() -> JsonFileHandler.saveAccounts(any())).thenAnswer(invocation -> null);
        Long id = accountService.openAccount("123invalid", "pass");
        assertNotNull(id); // still creates account
    }

    @Test
    void testFindByUserName_found() {
        Account acc = new Account("mariam", 1001L, 40.0, HashUtil.sha256("pass"));
        accountsStub.add(acc);

        Account found = accountService.findByUserName("mariam");
        assertEquals(acc, found);
    }

    @Test
    void testFindByUserName_notFound() {
        assertNull(accountService.findByUserName("unknown"));
    }

    @Test
    void testGetBalance_success() {
        Account acc = new Account("mariam", 1001L, 40.0, HashUtil.sha256("pass"));
        accountsStub.add(acc);

        Double balance = accountService.getBalance("mariam");
        assertEquals(40.0, balance);
    }

    @Test
    void testGetBalance_userNotFound_returnsNull() {
        assertNull(accountService.getBalance("not_exist"));
    }

    @Test
    void testUpdateAccount_savesAccounts() {
        mockedFileHandler.when(() -> JsonFileHandler.saveAccounts(any())).thenAnswer(invocation -> null);
        accountService.updateAccount(new Account());
        mockedFileHandler.verify(() -> JsonFileHandler.saveAccounts(any()), times(3));
    }

    @Test
    void testValidatePassword_correct() {
        String password = "secure";
        Account acc = new Account("karim", 1002L, 30.0, HashUtil.sha256(password));
        accountsStub.add(acc);

        assertTrue(accountService.validatePassword("karim", password));
    }

    @Test
    void testValidatePassword_wrong() {
        Account acc = new Account("karim", 1002L, 30.0, HashUtil.sha256("correct"));
        accountsStub.add(acc);

        assertFalse(accountService.validatePassword("karim", "wrong"));
    }

    @Test
    void testValidatePassword_userNotFound() {
        assertFalse(accountService.validatePassword("ghost", "any"));
    }
}
