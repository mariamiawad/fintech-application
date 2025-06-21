package model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.axis.fintech.model.Account;

class AccountTest {

    @Test
    void testDefaultConstructor_andSetters() {
        Account account = new Account();

        account.setAccountId(100L);
        account.setUserName("testuser");
        account.setBalance(500.0);
        account.setPasswordHash("hashed123");
        account.setTransactions(List.of(1L, 2L, 3L));

        assertEquals(100L, account.getAccountId());
        assertEquals("testuser", account.getUserName());
        assertEquals(500.0, account.getBalance());
        assertEquals("hashed123", account.getPasswordHash());
        assertEquals(List.of(1L, 2L, 3L), account.getTransactions());
    }

    @Test
    void testParameterizedConstructor_initializesCorrectly() {
        Account account = new Account("user1", 200L, 1000.0, "hash456");

        assertEquals("user1", account.getUserName());
        assertEquals(200L, account.getAccountId());
        assertEquals(1000.0, account.getBalance());
        assertEquals("hash456", account.getPasswordHash());
        assertNotNull(account.getTransactions());
        assertTrue(account.getTransactions().isEmpty());
        assertNotNull(account.getTimestamp());
        assertTrue(account.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testAddTransactionId_addsCorrectly() {
        Account account = new Account();
        account.addTransactionId(999L);

        assertEquals(1, account.getTransactions().size());
        assertEquals(999L, account.getTransactions().get(0));
    }
}
