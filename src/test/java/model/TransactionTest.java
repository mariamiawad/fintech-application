package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.axis.fintech.model.Transaction;
import com.axis.fintech.model.Transaction.TransactionType;
import com.axis.fintech.utils.IdGenerator;

class TransactionTest {

    @Test
    void testDefaultConstructor_andSetters() {
        Transaction tx = new Transaction();
        assertNull(tx.getTransactionId());
        assertNull(tx.getAmount());
        assertNull(tx.getAccountId());
        assertNull(tx.getType());
        assertNull(tx.getTimestamp());
    }

    @Test
    void testParameterizedConstructor_initializesCorrectly() {
        long expectedTxId = 999L;
        long accountId = 1001L;
        double amount = 200.0;
        TransactionType type = TransactionType.WITHDRAWAL;

        try (MockedStatic<IdGenerator> mocked = org.mockito.Mockito.mockStatic(IdGenerator.class)) {
            mocked.when(IdGenerator::nextTransactionId).thenReturn(expectedTxId);

            Transaction tx = new Transaction(amount, accountId, type);

            assertEquals(expectedTxId, tx.getTransactionId());
            assertEquals(accountId, tx.getAccountId());
            assertEquals(amount, tx.getAmount());
            assertEquals(type, tx.getType());
            assertNotNull(tx.getTimestamp());
            assertTrue(tx.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)));
        }
    }
}
