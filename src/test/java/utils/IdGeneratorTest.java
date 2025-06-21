package utils;

import com.axis.fintech.model.Account;
import com.axis.fintech.model.Transaction;
import com.axis.fintech.utils.IdGenerator;
import com.axis.fintech.utils.JsonFileHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
class IdGeneratorTest {


	@BeforeEach
	void resetIdGenerator() throws Exception {
	    Field accountIdField = IdGenerator.class.getDeclaredField("accountId");
	    Field transactionIdField = IdGenerator.class.getDeclaredField("transactionId");

	    accountIdField.setAccessible(true);
	    transactionIdField.setAccessible(true);

	    accountIdField.set(null, new AtomicLong(1000));
	    transactionIdField.set(null, new AtomicLong(1));
	}


    @Test
    void testIdGeneration_fromLoadedData() {
        List<Account> mockAccounts = List.of(
                new Account("user1", 1010L, 0.0, "hash")
        );
        List<Transaction> mockTransactions = List.of(
                new Transaction(100.0, 1010L, Transaction.TransactionType.DEPOSIT) {
                    {
                        // manually setting ID since constructor auto-generates
                        this.getClass().getDeclaredFields()[0].setAccessible(true);
                    }
                }
        );

        try (MockedStatic<JsonFileHandler> mocked = mockStatic(JsonFileHandler.class)) {
            mocked.when(JsonFileHandler::loadAccounts).thenReturn(mockAccounts);
            mocked.when(JsonFileHandler::loadTransactions).thenReturn(mockTransactions);

            // Re-initialize the IdGenerator by accessing a method (triggers static block indirectly)
            long newAccountId = IdGenerator.nextAccountId();
            long newTransactionId = IdGenerator.nextTransactionId();

            assertEquals(1000, newAccountId); // 1010L + 1
            assertEquals(2L, newTransactionId); // 1L + 1 (default init is 1)
        }
    }

    @Test
    void testIdGeneration_fromEmptyData() {
        try (MockedStatic<JsonFileHandler> mocked = mockStatic(JsonFileHandler.class)) {
            mocked.when(JsonFileHandler::loadAccounts).thenReturn(List.of());
            mocked.when(JsonFileHandler::loadTransactions).thenReturn(List.of());

            // Trigger static initialization with no data
            long accountId = IdGenerator.nextAccountId();
            long transactionId = IdGenerator.nextTransactionId();

            // Defaults are 1000 and 1 respectively
            assertEquals(1000L, accountId);
            assertEquals(1L, transactionId);
        }
    }
}
