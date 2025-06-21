package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.axis.fintech.model.Account;
import com.axis.fintech.service.AccountService;
import com.axis.fintech.service.TransactionService;
import com.axis.fintech.utils.JsonFileHandler;

class TransactionServiceTest {

    private AccountService accountService;
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        accountService = mock(AccountService.class);
        transactionService = new TransactionService(accountService);

        try (MockedStatic<JsonFileHandler> mockedStatic = mockStatic(JsonFileHandler.class)) {
            mockedStatic.when(JsonFileHandler::loadTransactions).thenReturn(new ArrayList<>());
            transactionService.init();
        }
    }


    @Test
    void testDeposit_userNotFound() {
        when(accountService.findByUserName("nonexistent")).thenReturn(null);
        Long txId = transactionService.deposit("nonexistent", 100.0);
        assertNull(txId);
    }

    @Test
    void testDeposit_valid() {
        Account acc = new Account("user1", 1000L, 50.0, "pass");
        when(accountService.findByUserName("user1")).thenReturn(acc);

        try (MockedStatic<JsonFileHandler> mockedStatic = mockStatic(JsonFileHandler.class)) {
            Long txId = transactionService.deposit("user1", 100.0);
            assertNotNull(txId);
            assertEquals(150.0, acc.getBalance());
            assertEquals(1, acc.getTransactions().size());
            verify(accountService).updateAccount(acc);
            mockedStatic.verify(() -> JsonFileHandler.saveTransactions(any()));
        }
    }

    @Test
    void testWithdraw_userNotFound() {
        when(accountService.findByUserName("ghost")).thenReturn(null);
        Long txId = transactionService.withdraw("ghost", 50.0);
        assertNull(txId);
    }

    @Test
    void testWithdraw_insufficientBalance() {
        Account acc = new Account("user1", 1000L, 30.0, "pass");
        when(accountService.findByUserName("user1")).thenReturn(acc);
        Long txId = transactionService.withdraw("user1", 50.0);
        assertNull(txId);
    }

    @Test
    void testWithdraw_valid() {
        Account acc = new Account("user1", 1000L, 100.0, "pass");
        when(accountService.findByUserName("user1")).thenReturn(acc);

        try (MockedStatic<JsonFileHandler> mockedStatic = mockStatic(JsonFileHandler.class)) {
            Long txId = transactionService.withdraw("user1", 40.0);
            assertNotNull(txId);
            assertEquals(60.0, acc.getBalance());
            assertEquals(1, acc.getTransactions().size());
            verify(accountService).updateAccount(acc);
            mockedStatic.verify(() -> JsonFileHandler.saveTransactions(any()));
        }
    }

    @Test
    void testGetAllTransactions_initiallyEmpty() {
        assertTrue(transactionService.getAllTransactions().isEmpty());
    }
}
