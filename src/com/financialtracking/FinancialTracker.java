package com.financialtracking;

import java.util.ArrayList;
import java.util.List;

public class FinancialTracker {
    private User user;
    private List<Transaction> transactions;
    private double balance;

    public FinancialTracker(User user) {
        this.user = user;
        this.transactions = new ArrayList<>();
        this.balance = 0.0;
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
        balance += amount;
        System.out.println("✓ Income of $" + String.format("%.2f", amount) + 
                         " recorded. New balance: $" + 
                         String.format("%.2f", balance));
    }

    /**
     * Add expense transaction
     */
    public void addExpense(double amount, String remark) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Expense amount must be positive");
        }

        if (amount > balance) {
            throw new FinancialException(
                "Insufficient balance. Current balance: $" + 
                String.format("%.2f", balance));
        }

        Transaction transaction = new Transaction(amount, 
            Transaction.TransactionType.EXPENSE, remark);
        transactions.add(transaction);
        balance -= amount;
        System.out.println("✓ Expense of $" + String.format("%.2f", amount) + 
                         " recorded. New balance: $" + 
                         String.format("%.2f", balance));
    }

    /**
     * Get current balance
     */
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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
     * Print financial summary
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

        System.out.println("\n" + "=".repeat(50));
        System.out.println(String.format("FINANCIAL SUMMARY FOR USER: %s", 
                                        user.getUsername()));
        System.out.println("=".repeat(50));
        System.out.println(String.format("Total Income:        $%.2f", totalIncome));
        System.out.println(String.format("Total Expense:       $%.2f", totalExpense));
        System.out.println(String.format("Current Balance:     $%.2f", balance));
        System.out.println(String.format("Total Transactions:  %d", transactions.size()));
        System.out.println("=".repeat(50) + "\n");
    }

    @Override
    public String toString() {
        return "FinancialTracker{" +
                "user=" + user +
                ", transactions=" + transactions.size() +
                ", balance=" + balance +
                '}';
    }
}
