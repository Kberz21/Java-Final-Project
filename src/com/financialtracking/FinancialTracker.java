package com.financialtracking;

import java.util.ArrayList;
import java.util.List;

public class FinancialTracker {
    private User user;
    private List<Transaction> transactions;
    private double checkingBalance;
    private double savingsBalance;
    private SavingsGoal savingsGoal;
    private SpendingLimit spendingLimit;

    public FinancialTracker(User user) {
        this.user = user;
        this.transactions = new ArrayList<>();
        this.checkingBalance = 0.0;
        this.savingsBalance = 0.0;
        this.savingsGoal = null;
        this.spendingLimit = null;
    }

    /**
     * Add income transaction
     */
    public void addIncome(double amount, String remark) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Income amount must be positive");
        }

        Transaction transaction = new Transaction(amount, 
            Transaction.TransactionType.INCOME, remark);
        transactions.add(transaction);
        checkingBalance += amount;
        System.out.println("✓ Income of $" + String.format("%.2f", amount) + 
                         " recorded. New balance: $" + 
                         String.format("%.2f", checkingBalance));
    }

    /**
     * Add expense transaction
     */
    public void addExpense(double amount, String remark) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Expense amount must be positive");
        }

        if (amount > checkingBalance) {
            throw new FinancialException(
                "Insufficient balance. Current balance: $" + 
                String.format("%.2f", checkingBalance));
        }

        // Check spending limit if it exists
        if (spendingLimit != null) {
            spendingLimit.checkAndResetIfNewMonth();
            double newSpending = spendingLimit.getCurrentSpending() + amount;

            if (newSpending >= spendingLimit.getLimitAmount() * 0.8 && 
                newSpending < spendingLimit.getLimitAmount()) {
                System.out.println("\n⚠️  WARNING: You are at " + 
                    String.format("%.1f%%", spendingLimit.getSpendingPercentage() + 
                    (amount / spendingLimit.getLimitAmount() * 100)) + 
                    " of your spending limit!");
                System.out.println("   Remaining budget: $" + 
                    String.format("%.2f", spendingLimit.getRemainingBudget() - amount));
            } else if (newSpending >= spendingLimit.getLimitAmount()) {
                System.out.println("\n⚠️  ALERT: You have reached or exceeded your spending limit!");
                System.out.println("   Current limit: $" + 
                    String.format("%.2f", spendingLimit.getLimitAmount()));
                System.out.println("   This transaction will take you over by: $" + 
                    String.format("%.2f", newSpending - spendingLimit.getLimitAmount()));
                System.out.println("   Proceeding with transaction...\n");
            }

            spendingLimit.addSpending(amount);
        }

        Transaction transaction = new Transaction(amount, 
            Transaction.TransactionType.EXPENSE, remark);
        transactions.add(transaction);
        checkingBalance -= amount;
        System.out.println("✓ Expense of $" + String.format("%.2f", amount) + 
                         " recorded. New balance: $" + 
                         String.format("%.2f", checkingBalance));
    }

    /**
     * Transfer money from checking to savings
     */
    public void transferToSavings(double amount, String remark) throws FinancialException {
        if (savingsGoal == null) {
            throw new FinancialException("No savings goal set. Please set a savings goal first.");
        }

        if (amount <= 0) {
            throw new FinancialException("Transfer amount must be positive");
        }

        if (amount > checkingBalance) {
            throw new FinancialException(
                "Insufficient checking balance. Current balance: $" + 
                String.format("%.2f", checkingBalance));
        }

        checkingBalance -= amount;
        savingsBalance += amount;
        savingsGoal.deposit(amount);

        System.out.println("✓ Transferred $" + String.format("%.2f", amount) + 
                         " to savings account");
        System.out.println("  Checking balance: $" + String.format("%.2f", checkingBalance));
        System.out.println("  Savings balance: $" + String.format("%.2f", savingsBalance));
    }

    /**
     * Transfer money from savings to checking
     */
    public void withdrawFromSavings(double amount, String remark) throws FinancialException {
        if (savingsGoal == null) {
            throw new FinancialException("No savings goal set. Cannot withdraw from savings.");
        }

        if (amount <= 0) {
            throw new FinancialException("Withdrawal amount must be positive");
        }

        if (amount > savingsBalance) {
            throw new FinancialException(
                "Insufficient savings balance. Current savings: $" + 
                String.format("%.2f", savingsBalance));
        }

        savingsBalance -= amount;
        checkingBalance += amount;
        savingsGoal.withdraw(amount);

        System.out.println("✓ Withdrew $" + String.format("%.2f", amount) + 
                         " from savings account");
        System.out.println("  Checking balance: $" + String.format("%.2f", checkingBalance));
        System.out.println("  Savings balance: $" + String.format("%.2f", savingsBalance));
    }

    /**
     * Set a savings goal
     */
    public void setSavingsGoal(String goalName, double targetAmount) throws FinancialException {
        this.savingsGoal = new SavingsGoal(goalName, targetAmount);
        System.out.println("✓ Savings goal set: " + savingsGoal);
    }

    /**
     * Set spending limit
     */
    public void setSpendingLimit(double limitAmount) throws FinancialException {
        this.spendingLimit = new SpendingLimit(limitAmount);
        System.out.println("✓ Spending limit set: $" + String.format("%.2f", limitAmount));
    }

    /**
     * Get checking balance
     */
    public double getCheckingBalance() {
        return checkingBalance;
    }

    public void setCheckingBalance(double balance) {
        this.checkingBalance = balance;
    }

    /**
     * Get savings balance
     */
    public double getSavingsBalance() {
        return savingsBalance;
    }

    public void setSavingsBalance(double balance) {
        this.savingsBalance = balance;
    }

    /**
     * Get all transactions
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public User getUser() {
        return user;
    }

    public SavingsGoal getSavingsGoal() {
        return savingsGoal;
    }

    public void setSavingsGoal(SavingsGoal goal) {
        this.savingsGoal = goal;
    }

    public SpendingLimit getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(SpendingLimit limit) {
        this.spendingLimit = limit;
    }

    /**
     * Print all transactions in formatted table
     */
    public void printAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }

        System.out.println("\n" + "=".repeat(90));
        System.out.println(String.format("TRANSACTION HISTORY FOR USER: %s", 
                                        user.getUsername()));
        System.out.println("=".repeat(90));
        System.out.println(String.format("| %3s | %-8s | %12s | %19s | %30s |",
                "ID", "Type", "Amount", "Timestamp", "Remark"));
        System.out.println("-".repeat(90));

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }

        System.out.println("-".repeat(90) + "\n");
    }

    /**
     * Print financial summary with dashboard
     */
    public void printSummary() {
        double totalIncome = 0;
        double totalExpense = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                totalIncome += transaction.getAmount();
            } else {
                totalExpense += transaction.getAmount();
            }
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println(String.format("FINANCIAL SUMMARY FOR USER: %s", 
                                        user.getUsername()));
        System.out.println("=".repeat(70));

        // Account Balances
        System.out.println("\n📊 ACCOUNT BALANCES:");
        System.out.println("-".repeat(70));
        System.out.println(String.format("  Checking Balance:       $%.2f", checkingBalance));
        System.out.println(String.format("  Savings Balance:        $%.2f", savingsBalance));
        System.out.println(String.format("  Total Balance:          $%.2f", checkingBalance + savingsBalance));

        // Transaction Summary
        System.out.println("\n💰 TRANSACTION SUMMARY:");
        System.out.println("-".repeat(70));
        System.out.println(String.format("  Total Income:           $%.2f", totalIncome));
        System.out.println(String.format("  Total Expense:          $%.2f", totalExpense));
        System.out.println(String.format("  Net Balance:            $%.2f", totalIncome - totalExpense));
        System.out.println(String.format("  Total Transactions:     %d", transactions.size()));

        // Savings Goal Dashboard
        if (savingsGoal != null) {
            System.out.println("\n🎯 SAVINGS GOAL DASHBOARD:");
            System.out.println("-".repeat(70));
            System.out.println(String.format("  Goal Name:              %s", savingsGoal.getGoalName()));
            System.out.println(String.format("  Target Amount:          $%.2f", savingsGoal.getTargetAmount()));
            System.out.println(String.format("  Current Savings:        $%.2f", savingsGoal.getCurrentSavings()));
            System.out.println(String.format("  Remaining Amount:       $%.2f", savingsGoal.getRemainingAmount()));
            
            double progress = savingsGoal.getProgressPercentage();
            String progressBar = generateProgressBar(progress);
            System.out.println(String.format("  Progress:               %s (%.1f%%)", progressBar, progress));
            
            if (savingsGoal.isGoalAchieved()) {
                System.out.println("  Status:                 ✅ GOAL ACHIEVED!");
            }
        } else {
            System.out.println("\n🎯 SAVINGS GOAL DASHBOARD:");
            System.out.println("-".repeat(70));
            System.out.println("  No savings goal set yet.");
        }

        // Spending Limit Dashboard
        if (spendingLimit != null) {
            try {
                spendingLimit.checkAndResetIfNewMonth();
                System.out.println("\n💸 SPENDING LIMIT DASHBOARD:");
                System.out.println("-".repeat(70));
                System.out.println(String.format("  Monthly Limit:          $%.2f", spendingLimit.getLimitAmount()));
                System.out.println(String.format("  Current Spending:       $%.2f", spendingLimit.getCurrentSpending()));
                System.out.println(String.format("  Remaining Budget:       $%.2f", spendingLimit.getRemainingBudget()));
                
                double spending = spendingLimit.getSpendingPercentage();
                String spendingBar = generateProgressBar(spending);
                System.out.println(String.format("  Progress:               %s (%.1f%%)", spendingBar, spending));
                
                if (spendingLimit.isAtOrExceededLimit()) {
                    System.out.println(String.format("  Status:                 ⚠️  EXCEEDED by $%.2f", 
                                                    spendingLimit.getOverageAmount()));
                } else if (spendingLimit.isAt80Percent()) {
                    System.out.println("  Status:                 ⚠️  APPROACHING LIMIT (80%+)");
                } else {
                    System.out.println("  Status:                 ✅ On track");
                }
            } catch (FinancialException e) {
                System.err.println("Error displaying spending limit: " + e.getMessage());
            }
        } else {
            System.out.println("\n💸 SPENDING LIMIT DASHBOARD:");
            System.out.println("-".repeat(70));
            System.out.println("  No spending limit set yet.");
        }

        System.out.println("\n" + "=".repeat(70) + "\n");
    }

    /**
     * Generate visual progress bar
     */
    private String generateProgressBar(double percentage) {
        int filled = (int) (percentage / 5);
        int empty = 20 - filled;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        for (int i = 0; i < empty; i++) {
            bar.append("░");
        }
        bar.append("]");
        return bar.toString();
    }

    @Override
    public String toString() {
        return "FinancialTracker{" +
                "user=" + user +
                ", checkingBalance=" + checkingBalance +
                ", savingsBalance=" + savingsBalance +
                ", transactions=" + transactions.size() +
                '}';
    }
}