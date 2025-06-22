package com.axis.fintech.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.axis.fintech.model.Account;
import com.axis.fintech.model.Transaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonFileHandler {
	private static final String ACCOUNTS_PATH;
	private static final String TRANSACTIONS_PATH;

	static {
		ACCOUNTS_PATH = loadPathFromClassProperties("accounts.path");
		TRANSACTIONS_PATH = loadPathFromClassProperties("transactions.path");
	}

	private static String loadPathFromClassProperties(String key) {
		try (InputStream input = JsonFileHandler.class.getClassLoader().getResourceAsStream("class.properties")) {
			if (input == null) {
				throw new RuntimeException("class.properties not found in resources");
			}
			Properties props = new Properties();
			props.load(input);
			String value = props.getProperty(key);
			if (value == null || value.isBlank()) {
				throw new RuntimeException("Key '" + key + "' not found or empty in class.properties");
			}
			return value;
		} catch (Exception e) {
			throw new RuntimeException("Failed to load class.properties: " + key, e);
		}
	}

	// === Accounts ===

	public static List<Account> loadAccounts() {
	    File file = Path.of(ACCOUNTS_PATH).toFile();

	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.findAndRegisterModules();
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        // Create file if it doesn't exist
	        if (!file.exists()) {
	            System.out.println("Creating new accounts file at: " + file.getAbsolutePath());
	            file.getParentFile().mkdirs(); // ensure parent directories exist
	            file.createNewFile();

	            Map<String, List<Account>> wrapper = new HashMap<>();
	            wrapper.put("accounts", new ArrayList<>());
	            mapper.writeValue(file, wrapper);
	            return new ArrayList<>();
	        }

	        // If file exists but is empty, initialize with empty wrapper
	        if (file.length() == 0) {
	            Map<String, List<Account>> wrapper = new HashMap<>();
	            wrapper.put("accounts", new ArrayList<>());
	            mapper.writeValue(file, wrapper);
	            return new ArrayList<>();
	        }

	        // File exists and is non-empty, read normally
	        Map<String, List<Account>> wrapper = mapper.readValue(file, new TypeReference<>() {});
	        return wrapper.getOrDefault("accounts", new ArrayList<>());

	    } catch (IOException e) {
	        System.err.println("❌ Failed to load accounts file: " + file.getAbsolutePath());
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}


	public static void saveAccounts(List<Account> accounts) {
		File file = Path.of(ACCOUNTS_PATH).toFile();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.findAndRegisterModules();
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);

			Map<String, List<Account>> wrapper = new HashMap<>();

			wrapper.put("accounts", accounts);
			mapper.writeValue(file, wrapper);

		} catch (Exception e) {
			throw new RuntimeException("Failed to write transactions JSON", e);
		}

	}

	public static List<Transaction> loadTransactions() {
	    File file = Path.of(TRANSACTIONS_PATH).toFile();

	    try {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.findAndRegisterModules();
	        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	        // Create file and initialize if it doesn't exist
	        if (!file.exists()) {
	            System.out.println("Creating new transactions file at: " + file.getAbsolutePath());
	            file.getParentFile().mkdirs();
	            file.createNewFile();

	            Map<String, List<Transaction>> wrapper = new HashMap<>();
	            wrapper.put("transactions", new ArrayList<>());
	            mapper.writeValue(file, wrapper);
	            return new ArrayList<>();
	        }

	        // Initialize empty file if needed
	        if (file.length() == 0) {
	            Map<String, List<Transaction>> wrapper = new HashMap<>();
	            wrapper.put("transactions", new ArrayList<>());
	            mapper.writeValue(file, wrapper);
	            return new ArrayList<>();
	        }

	        // Normal read
	        Map<String, List<Transaction>> wrapper = mapper.readValue(file, new TypeReference<>() {});
	        return wrapper.getOrDefault("transactions", new ArrayList<>());

	    } catch (IOException e) {
	        System.err.println("❌ Failed to read transactions file (" + TRANSACTIONS_PATH + "): " + e.getMessage());
	        return new ArrayList<>();
	    }
	}

	public static void saveTransactions(List<Transaction> transactions) {
		File file = Path.of(TRANSACTIONS_PATH).toFile();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.findAndRegisterModules();
			mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
			mapper.enable(SerializationFeature.INDENT_OUTPUT);

			Map<String, List<Transaction>> wrapper = new HashMap<>();
			wrapper.put("transactions", transactions);
			mapper.writeValue(file, wrapper);
		} catch (Exception e) {
			throw new RuntimeException("Failed to write transactions JSON", e);
		}
	}

}
