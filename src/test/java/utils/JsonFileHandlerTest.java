package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.axis.fintech.model.Account;
import com.axis.fintech.model.Transaction;
import com.axis.fintech.model.Transaction.TransactionType;
import com.axis.fintech.utils.JsonFileHandler;

class JsonFileHandlerTest {

    @TempDir
    static Path tempDir;

    static Path accountsPath;
    static Path transactionsPath;

    @BeforeAll
    static void setupPropertiesFile() throws Exception {
        accountsPath = tempDir.resolve("accounts.json");
        transactionsPath = tempDir.resolve("transactions.json");

        try (PrintWriter writer = new PrintWriter(new FileWriter(tempDir.resolve("class.properties").toFile()))) {
            writer.println("accounts.path=" + accountsPath.toString().replace("\\", "/"));
            writer.println("transactions.path=" + transactionsPath.toString().replace("\\", "/"));
        }

        // Override class loader to point to our temporary properties
        System.setProperty("java.class.path", tempDir.toString());
    }

    @BeforeEach
    void resetDataFiles() throws Exception {
        JsonFileHandler.saveAccounts(List.of());
        JsonFileHandler.saveTransactions(List.of());
    }

    @Test
    void testSaveAndLoadAccounts() {
        Account acc = new Account("user1", 1001L, 100.0, "hashed");
        JsonFileHandler.saveAccounts(List.of(acc));

        List<Account> result = JsonFileHandler.loadAccounts();
        assertEquals(1, result.size());
        assertEquals("user1", result.get(0).getUserName());
    }

    @Test
    void testSaveAndLoadTransactions() {
        Transaction tx = new Transaction(50.0, 1001L, TransactionType.DEPOSIT);
        JsonFileHandler.saveTransactions(List.of(tx));

        List<Transaction> result = JsonFileHandler.loadTransactions();
        assertEquals(1, result.size());
        assertEquals(50.0, result.get(0).getAmount());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getType());
    }

    @Test
    void testLoadAccounts_emptyFile_returnsEmptyList() throws Exception {
        JsonFileHandler.saveAccounts(List.of());
        assertTrue(JsonFileHandler.loadAccounts().isEmpty());
    }

    @Test
    void testLoadTransactions_emptyFile_returnsEmptyList() throws Exception {
        JsonFileHandler.saveTransactions(List.of());
        assertTrue(JsonFileHandler.loadTransactions().isEmpty());
    }
}
