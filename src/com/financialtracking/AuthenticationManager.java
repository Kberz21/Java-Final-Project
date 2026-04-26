package com.financialtracking;

import java.util.*;

public class AuthenticationManager {
    private Map<String, User> users;
    private Map<String, FinancialTracker> trackers;
    private Map<String, List<Transaction>> loadedTransactions;

    public AuthenticationManager() throws FileOperationException {
        this.users = FileManager.loadAllUsers();
        this.trackers = new HashMap<>();
        this.loadedTransactions = new HashMap<>();
        
        // Load trackers for all existing users
        for (String username : users.keySet()) {
            try {
                loadUserTracker(username);
            } catch (AuthenticationException e) {
                System.err.println("Warning: Failed to load tracker for " + username + ": " + e.getMessage());
            }
        }
    }

    /**
     * Register a new user
     */
    public User registerUser(String username, String password) 
            throws AuthenticationException {
        try {
            if (username == null || username.trim().isEmpty()) {
                throw new AuthenticationException("Username cannot be empty");
            }

            if (password == null || password.trim().isEmpty()) {
                throw new AuthenticationException("Password cannot be empty");
            }

            if (password.length() < 4) {
                throw new AuthenticationException(
                    "Password must be at least 4 characters");
            }

            if (users.containsKey(username)) {
                throw new AuthenticationException(
                    "Username '" + username + "' already exists");
            }

            User newUser = new User(username, password);
            users.put(username, newUser);
            
            // Save to file
            FileManager.saveUser(newUser);
            
            // Create tracker for new user with EMPTY transaction list
            FinancialTracker tracker = new FinancialTracker(newUser);
            trackers.put(username, tracker);
            loadedTransactions.put(username, new ArrayList<>());

            System.out.println("✓ User '" + username + "' registered successfully!");
            return newUser;
        } catch (FileOperationException e) {
            throw new AuthenticationException(
                "Error registering user: " + e.getMessage(), e);
        }
    }

    /**
     * Login user
     */
    public User loginUser(String username, String password) 
            throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username cannot be empty");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Password cannot be empty");
        }

        if (!users.containsKey(username)) {
            throw new AuthenticationException(
                "User '" + username + "' not found");
        }

        User user = users.get(username);
        if (!user.verifyPassword(password)) {
            throw new AuthenticationException("Incorrect password");
        }

        System.out.println("✓ Welcome back, " + username + "!");
        return user;
    }

    /**
     * Get financial tracker for user
     */
    public FinancialTracker getTracker(String username) 
            throws AuthenticationException, FileOperationException {
        if (!users.containsKey(username)) {
            throw new AuthenticationException(
                "User '" + username + "' not found");
        }

        if (!trackers.containsKey(username)) {
            loadUserTracker(username);
        }

        return trackers.get(username);
    }

        /**
     * Load user tracker from file (ONLY called once per user session)
     */
    private void loadUserTracker(String username) 
            throws FileOperationException, AuthenticationException {
        User user = users.get(username);
        if (user == null) {
            throw new AuthenticationException("User not found");
        }

        if (trackers.containsKey(username)) {
            return;
        }

        FinancialTracker tracker = new FinancialTracker(user);
        List<Transaction> transactions = FileManager.loadTransactions(username);
        
        tracker.setTransactions(transactions);
        loadedTransactions.put(username, new ArrayList<>(transactions));
        
        // Load savings goal if exists
        try {
            SavingsGoal savingsGoal = FileManager.loadSavingsGoal(username);
            if (savingsGoal != null) {
                tracker.setSavingsGoal(savingsGoal);
            }
        } catch (FileOperationException e) {
            System.err.println("Warning: Could not load savings goal: " + e.getMessage());
        }

        // Load spending limit if exists
        try {
            SpendingLimit spendingLimit = FileManager.loadSpendingLimit(username);
            if (spendingLimit != null) {
                tracker.setSpendingLimit(spendingLimit);
            }
        } catch (FileOperationException e) {
            System.err.println("Warning: Could not load spending limit: " + e.getMessage());
        }
        
        // Recalculate balances
        double checkingBalance = 0;
        double savingsBalance = 0;
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                checkingBalance += transaction.getAmount();
            } else {
                checkingBalance -= transaction.getAmount();
            }
        }
        
        // Adjust balances based on current savings goal
        if (tracker.getSavingsGoal() != null) {
            savingsBalance = tracker.getSavingsGoal().getCurrentSavings();
        }
        
        tracker.setCheckingBalance(checkingBalance);
        tracker.setSavingsBalance(savingsBalance);
        
        trackers.put(username, tracker);
        
        System.out.println("✓ Loaded " + transactions.size() + " transactions for " + username);
    }

        /**
     * Save all data for a user
     */
    public void saveUserData(String username) throws FileOperationException, AuthenticationException {
        FinancialTracker tracker = trackers.get(username);
        if (tracker == null) {
            System.err.println("✗ Tracker not found for user: " + username);
            return;
        }

        FileManager.saveUser(tracker.getUser());
        
        // Save transactions
        List<Transaction> allTransactions = tracker.getTransactions();
        FileManager.saveAllTransactions(username, allTransactions);
        
        // Save savings goal
        FileManager.saveSavingsGoal(username, tracker.getSavingsGoal());
        
        // Save spending limit
        FileManager.saveSpendingLimit(username, tracker.getSpendingLimit());
        
        // Update the loaded state
        loadedTransactions.put(username, new ArrayList<>(allTransactions));
        
        System.out.println("✓ Data saved for user: " + username);
    }
}