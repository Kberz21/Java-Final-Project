package com.financialtracking;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileManager {
    private static final String DATA_DIRECTORY = "data";
    private static final String USERS_FILE = "data/users.txt";

    /**
     * Initialize data directory and files
     */
    public static void initializeDataDirectory() throws FileOperationException {
        try {
            Path dataPath = Paths.get(DATA_DIRECTORY);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("✓ Data directory created");
            }

            Path usersPath = Paths.get(USERS_FILE);
            if (!Files.exists(usersPath)) {
                Files.createFile(usersPath);
                System.out.println("✓ Users file created");
            }
        } catch (IOException e) {
            throw new FileOperationException(
                "Error initializing data directory: " + e.getMessage(), e);
        }
    }

    /**
     * Save user to file
     */
    public static void saveUser(User user) throws FileOperationException {
        try {
            List<String> lines = new ArrayList<>();
            
            if (Files.exists(Paths.get(USERS_FILE))) {
                lines = Files.readAllLines(Paths.get(USERS_FILE));
            }

            boolean userExists = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(user.getUsername() + "|")) {
                    lines.set(i, formatUserLine(user));
                    userExists = true;
                    break;
                }
            }

            if (!userExists) {
                lines.add(formatUserLine(user));
            }

            Files.write(Paths.get(USERS_FILE), lines);
        } catch (IOException e) {
            throw new FileOperationException(
                "Error saving user: " + e.getMessage(), e);
        }
    }

    /**
     * Load all users from file
     */
    public static Map<String, User> loadAllUsers() throws FileOperationException {
        Map<String, User> users = new HashMap<>();

        try {
            if (!Files.exists(Paths.get(USERS_FILE))) {
                return users;
            }

            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                User user = parseUserLine(line);
                users.put(user.getUsername(), user);
            }
        } catch (IOException e) {
            throw new FileOperationException(
                "Error loading users: " + e.getMessage(), e);
        }

        return users;
    }

    /**
     * Format user for file storage
     */
    private static String formatUserLine(User user) {
        return String.format("%s|%s|%s",
                user.getUsername(),
                user.getPasswordHash(),
                user.getCreatedAt());
    }

    /**
     * Parse user from file format
     */
    private static User parseUserLine(String line) throws FileOperationException {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 2) {
                throw new FileOperationException("Invalid user format");
            }

            String username = parts[0];
            String passwordHash = parts[1];

            User user = new User(username, "");
            user.setPasswordHash(passwordHash);

            return user;
        } catch (Exception e) {
            throw new FileOperationException(
                "Error parsing user: " + e.getMessage(), e);
        }
    }

    /**
     * Save ALL transactions for a user at once
     * This OVERWRITES the entire file (no appending!)
     */
    public static void saveAllTransactions(String username, 
            List<Transaction> transactions) throws FileOperationException {
        try {
            String userTransactionFile = String.format("data/%s_transactions.txt", username);
            List<String> lines = new ArrayList<>();

            // Convert all transactions to file format
            for (Transaction transaction : transactions) {
                lines.add(transaction.toFileFormat());
            }

            // Write ALL transactions at once (completely overwrites file)
            Files.write(Paths.get(userTransactionFile), lines);
            
            System.out.println("✓ Saved " + transactions.size() + 
                             " transactions to file");
        } catch (IOException e) {
            throw new FileOperationException(
                "Error saving transactions: " + e.getMessage(), e);
        }
    }

    /**
     * Load transactions for a user
     */
    public static List<Transaction> loadTransactions(String username) 
            throws FileOperationException {
        List<Transaction> transactions = new ArrayList<>();

        try {
            String userTransactionFile = String.format("data/%s_transactions.txt", username);
            
            if (!Files.exists(Paths.get(userTransactionFile))) {
                return transactions;
            }

            List<String> lines = Files.readAllLines(Paths.get(userTransactionFile));
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                try {
                    Transaction transaction = Transaction.fromFileFormat(line);
                    transactions.add(transaction);
                } catch (FinancialException e) {
                    System.err.println("Warning: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new FileOperationException(
                "Error loading transactions: " + e.getMessage(), e);
        }

        return transactions;
    }

    /**
     * Delete user account and associated data
     */
    public static void deleteUser(String username) throws FileOperationException {
        try {
            if (Files.exists(Paths.get(USERS_FILE))) {
                List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
                lines.removeIf(line -> line.startsWith(username + "|"));
                Files.write(Paths.get(USERS_FILE), lines);
            }

            String userTransactionFile = String.format("data/%s_transactions.txt", username);
            Files.deleteIfExists(Paths.get(userTransactionFile));
            
            System.out.println("✓ Deleted all data for user: " + username);
        } catch (IOException e) {
            throw new FileOperationException(
                "Error deleting user: " + e.getMessage(), e);
        }
    }
        /**
     * Save savings goal for a user
     */
    public static void saveSavingsGoal(String username, SavingsGoal goal) 
            throws FileOperationException {
        try {
            String goalFile = String.format("data/%s_savings_goal.txt", username);
            if (goal == null) {
                Files.deleteIfExists(Paths.get(goalFile));
                return;
            }
            List<String> lines = new ArrayList<>();
            lines.add(goal.toFileFormat());
            Files.write(Paths.get(goalFile), lines);
            System.out.println("✓ Savings goal saved");
        } catch (IOException e) {
            throw new FileOperationException(
                "Error saving savings goal: " + e.getMessage(), e);
        }
    }

    /**
     * Load savings goal for a user
     */
    public static SavingsGoal loadSavingsGoal(String username) 
            throws FileOperationException {
        try {
            String goalFile = String.format("data/%s_savings_goal.txt", username);
            
            if (!Files.exists(Paths.get(goalFile))) {
                return null;
            }

            List<String> lines = Files.readAllLines(Paths.get(goalFile));
            if (lines.isEmpty()) {
                return null;
            }

            return SavingsGoal.fromFileFormat(lines.get(0));
        } catch (IOException e) {
            throw new FileOperationException(
                "Error loading savings goal: " + e.getMessage(), e);
        } catch (FinancialException e) {
            throw new FileOperationException(
                "Error parsing savings goal: " + e.getMessage(), e);
        }
    }

    /**
     * Save spending limit for a user
     */
    public static void saveSpendingLimit(String username, SpendingLimit limit) 
            throws FileOperationException {
        try {
            String limitFile = String.format("data/%s_spending_limit.txt", username);
            if (limit == null) {
                Files.deleteIfExists(Paths.get(limitFile));
                return;
            }
            List<String> lines = new ArrayList<>();
            lines.add(limit.toFileFormat());
            Files.write(Paths.get(limitFile), lines);
            System.out.println("✓ Spending limit saved");
        } catch (IOException e) {
            throw new FileOperationException(
                "Error saving spending limit: " + e.getMessage(), e);
        }
    }

    /**
     * Load spending limit for a user
     */
    public static SpendingLimit loadSpendingLimit(String username) 
            throws FileOperationException {
        try {
            String limitFile = String.format("data/%s_spending_limit.txt", username);
            
            if (!Files.exists(Paths.get(limitFile))) {
                return null;
            }

            List<String> lines = Files.readAllLines(Paths.get(limitFile));
            if (lines.isEmpty()) {
                return null;
            }

            return SpendingLimit.fromFileFormat(lines.get(0));
        } catch (IOException e) {
            throw new FileOperationException(
                "Error loading spending limit: " + e.getMessage(), e);
        } catch (FinancialException e) {
            throw new FileOperationException(
                "Error parsing spending limit: " + e.getMessage(), e);
        }
    }
}