package com.financialtracking;

import java.util.Scanner;

public class FinancialTrackerApp {
    private AuthenticationManager authManager;
    private FinancialTracker currentTracker;
    private String currentUsername;
    private Scanner scanner;

    public FinancialTrackerApp() throws FileOperationException {
        this.scanner = new Scanner(System.in);
        FileManager.initializeDataDirectory();
        this.authManager = new AuthenticationManager();
    }

    /**
     * Display main menu
     */
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINANCIAL TRACKING APP");
        System.out.println("=".repeat(50));
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.println("=".repeat(50));
    }

    /**
     * Tracker menu for logged-in user
     */
    private void trackerMenu() {
        boolean loggedIn = true;

        while (loggedIn) {
            try {
                displayTrackerMenu();
                System.out.print("Select an option: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleAddIncome();
                        break;
                    case "2":
                        handleAddExpense();
                        break;
                    case "3":
                        displayBalance();
                        break;
                    case "4":
                        currentTracker.printAllTransactions();
                        break;
                    case "5":
                        currentTracker.printSummary();
                        break;
                    case "6":
                        handleSavingsLimitsMenu();
                        break;
                    case "7":
                        try {
                            authManager.saveUserData(currentUsername);
                        } catch (FileOperationException | AuthenticationException e) {
                            System.err.println("✗ Error saving data: " + e.getMessage());
                        }
                        System.out.println("✓ Logged out successfully!");
                        currentTracker = null;
                        currentUsername = null;
                        loggedIn = false;
                        break;
                    default:
                        System.out.println("✗ Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
            }
        }
    }

    /**
     * Display current balance (updated)
     */
    private void displayBalance() {
        double checkingBalance = currentTracker.getCheckingBalance();
        double savingsBalance = currentTracker.getSavingsBalance();
        System.out.println("\n✓ Checking Balance: $" + String.format("%.2f", checkingBalance));
        System.out.println("✓ Savings Balance:  $" + String.format("%.2f", savingsBalance));
        System.out.println("✓ Total Balance:    $" + 
                          String.format("%.2f", checkingBalance + savingsBalance));
    }

    /**
     * Handle user registration
     */
    private void handleRegister() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            authManager.registerUser(username, password);
        } catch (AuthenticationException e) {
            System.err.println("✗ Registration failed: " + e.getMessage());
        }
    }

    /**
     * Handle user login
     */
    private void handleLogin() {
        try {
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            authManager.loginUser(username, password);
            currentUsername = username;
            currentTracker = authManager.getTracker(username);
            trackerMenu();
        } catch (AuthenticationException | FileOperationException e) {
            System.err.println("✗ Login failed: " + e.getMessage());
        }
    }

    /**
     * Handle adding income
     */
    private void handleAddIncome() {
        try {
            System.out.print("Enter income amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter remark (optional): ");
            String remark = scanner.nextLine().trim();

            currentTracker.addIncome(amount, remark);
            
            // Save data after adding income
            try {
                authManager.saveUserData(currentUsername);
            } catch (FileOperationException | AuthenticationException e) {
                System.err.println("✗ Error saving data: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Handle adding expense
     */
    private void handleAddExpense() {
        try {
            System.out.print("Enter expense amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter remark (optional): ");
            String remark = scanner.nextLine().trim();

            currentTracker.addExpense(amount, remark);
            
            // Save data after adding expense
            try {
                authManager.saveUserData(currentUsername);
            } catch (FileOperationException | AuthenticationException e) {
                System.err.println("✗ Error saving data: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }


    /**
     * Main application loop
     */
    public void run() {
        System.out.println("\n🎉 Welcome to Financial Tracking App!\n");

        boolean running = true;
        while (running) {
            try {
                displayMainMenu();
                System.out.print("Select an option: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleRegister();
                        break;
                    case "2":
                        handleLogin();
                        break;
                    case "3":
                        System.out.println("Thank you for using Financial Tracking App. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("✗ Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("✗ An unexpected error occurred: " + e.getMessage());
            }
        }

        scanner.close();
    }
        /**
     * Display new menu for savings and limits
     */
    private void displayTrackerMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Welcome, " + currentUsername + "!");
        System.out.println("Checking: $" + String.format("%.2f", currentTracker.getCheckingBalance()) +
                          " | Savings: $" + String.format("%.2f", currentTracker.getSavingsBalance()));
        System.out.println("=".repeat(50));
        System.out.println("1. Add Income");
        System.out.println("2. Add Expense");
        System.out.println("3. View Balance");
        System.out.println("4. View All Transactions");
        System.out.println("5. View Summary");
        System.out.println("6. Savings & Limits Management");
        System.out.println("7. Logout");
        System.out.println("=".repeat(50));
    }

    /**
     * Display savings and limits menu
     */
    private void displaySavingsLimitsMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("SAVINGS & LIMITS MANAGEMENT");
        System.out.println("=".repeat(50));
        System.out.println("1. Set Savings Goal");
        System.out.println("2. Transfer to Savings");
        System.out.println("3. Withdraw from Savings");
        System.out.println("4. Set Spending Limit");
        System.out.println("5. Back to Main Menu");
        System.out.println("=".repeat(50));
    }

    /**
     * Handle savings and limits menu
     */
    private void handleSavingsLimitsMenu() {
        boolean inMenu = true;

        while (inMenu) {
            try {
                displaySavingsLimitsMenu();
                System.out.print("Select an option: ");
                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        handleSetSavingsGoal();
                        break;
                    case "2":
                        handleTransferToSavings();
                        break;
                    case "3":
                        handleWithdrawFromSavings();
                        break;
                    case "4":
                        handleSetSpendingLimit();
                        break;
                    case "5":
                        inMenu = false;
                        break;
                    default:
                        System.out.println("✗ Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("✗ Error: " + e.getMessage());
            }
        }
    }

    /**
     * Handle setting savings goal
     */
    private void handleSetSavingsGoal() {
        try {
            System.out.print("Enter goal name (e.g., 'Vacation', 'Car', 'Emergency Fund'): ");
            String goalName = scanner.nextLine().trim();

            System.out.print("Enter target amount: $");
            double targetAmount = Double.parseDouble(scanner.nextLine().trim());

            currentTracker.setSavingsGoal(goalName, targetAmount);
            authManager.saveUserData(currentUsername);
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException | FileOperationException | AuthenticationException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Handle transfer to savings
     */
    private void handleTransferToSavings() {
        try {
            if (currentTracker.getSavingsGoal() == null) {
                System.out.println("✗ No savings goal set. Please set a savings goal first.");
                return;
            }

            System.out.print("Enter amount to transfer to savings: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter remark (optional): ");
            String remark = scanner.nextLine().trim();

            currentTracker.transferToSavings(amount, remark);
            authManager.saveUserData(currentUsername);
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException | FileOperationException | AuthenticationException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Handle withdraw from savings
     */
    private void handleWithdrawFromSavings() {
        try {
            if (currentTracker.getSavingsGoal() == null) {
                System.out.println("✗ No savings account. Please set a savings goal first.");
                return;
            }

            System.out.print("Enter amount to withdraw from savings: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Enter remark (optional): ");
            String remark = scanner.nextLine().trim();

            currentTracker.withdrawFromSavings(amount, remark);
            authManager.saveUserData(currentUsername);
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException | FileOperationException | AuthenticationException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }

    /**
     * Handle setting spending limit
     */
    private void handleSetSpendingLimit() {
        try {
            System.out.print("Enter monthly spending limit: $");
            double limitAmount = Double.parseDouble(scanner.nextLine().trim());

            currentTracker.setSpendingLimit(limitAmount);
            authManager.saveUserData(currentUsername);
        } catch (NumberFormatException e) {
            System.err.println("✗ Invalid amount. Please enter a valid number.");
        } catch (FinancialException | FileOperationException | AuthenticationException e) {
            System.err.println("✗ Error: " + e.getMessage());
        }
    }
}