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

        // Check if already loaded in this session
        if (trackers.containsKey(username)) {
            return;
        }

        FinancialTracker tracker = new FinancialTracker(user);
        List<Transaction> transactions = FileManager.loadTransactions(username);
        
        tracker.setTransactions(transactions);
        loadedTransactions.put(username, new ArrayList<>(transactions));
        
        // Recalculate balance
        double balance = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                balance += transaction.getAmount();
            } else {
                balance -= transaction.getAmount();
            }
        }
        tracker.setBalance(balance);
        
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
        
        // Get all transactions from tracker
        List<Transaction> allTransactions = tracker.getTransactions();
        
        // Get previously loaded transactions
        List<Transaction> previouslyLoaded = loadedTransactions.getOrDefault(username, new ArrayList<>());
        
        System.out.println("DEBUG: Previously loaded: " + previouslyLoaded.size() + 
                         " transactions, Current: " + allTransactions.size() + " transactions");
        
        // Save the complete list (not appending)
        FileManager.saveAllTransactions(username, allTransactions);
        
        // Update the loaded state
        loadedTransactions.put(username, new ArrayList<>(allTransactions));
        
        System.out.println("✓ Data saved for user: " + username);
    }

    /**
     * Delete user account
     */
    public void deleteUserAccount(String username) throws FileOperationException {
        users.remove(username);
        trackers.remove(username);
        loadedTransactions.remove(username);
        FileManager.deleteUser(username);
        System.out.println("✓ User account deleted successfully");
    }

    /**
     * Check if user exists
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}