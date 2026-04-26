package com.financialtracking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SavingsGoal implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String goalName;
    private double targetAmount;
    private double currentSavings;
    private LocalDateTime createdAt;

    public SavingsGoal(String goalName, double targetAmount) throws FinancialException {
        if (goalName == null || goalName.trim().isEmpty()) {
            throw new FinancialException("Goal name cannot be empty");
        }
        if (targetAmount <= 0) {
            throw new FinancialException("Target amount must be positive");
        }

        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentSavings = 0.0;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Add money to savings goal
     */
    public void deposit(double amount) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Deposit amount must be positive");
        }
        this.currentSavings += amount;
    }

    /**
     * Withdraw money from savings goal
     */
    public void withdraw(double amount) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Withdrawal amount must be positive");
        }
        if (amount > this.currentSavings) {
            throw new FinancialException(
                "Insufficient savings. Current savings: $" + 
                String.format("%.2f", currentSavings));
        }
        this.currentSavings -= amount;
    }

    /**
     * Get progress percentage
     */
    public double getProgressPercentage() {
        if (targetAmount == 0) {
            return 0;
        }
        return (currentSavings / targetAmount) * 100;
    }

    /**
     * Check if goal is achieved
     */
    public boolean isGoalAchieved() {
        return currentSavings >= targetAmount;
    }

    /**
     * Get remaining amount needed
     */
    public double getRemainingAmount() {
        return Math.max(0, targetAmount - currentSavings);
    }

    /**
     * Format for file storage
     */
    public String toFileFormat() {
        return String.format("%s|%.2f|%.2f|%s",
                goalName, targetAmount, currentSavings, createdAt.format(formatter));
    }

    /**
     * Parse from file format
     */
    public static SavingsGoal fromFileFormat(String line) throws FinancialException {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 4) {
                throw new FinancialException("Invalid savings goal format");
            }

            String goalName = parts[0];
            double targetAmount = Double.parseDouble(parts[1]);
            double currentSavings = Double.parseDouble(parts[2]);
            LocalDateTime createdAt = LocalDateTime.parse(parts[3], formatter);

            SavingsGoal goal = new SavingsGoal(goalName, targetAmount);
            goal.currentSavings = currentSavings;

            return goal;
        } catch (Exception e) {
            throw new FinancialException("Error parsing savings goal: " + e.getMessage(), e);
        }
    }

    // Getters and Setters
    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentSavings() {
        return currentSavings;
    }

    public void setCurrentSavings(double currentSavings) {
        this.currentSavings = currentSavings;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return String.format("Goal: %s | Target: $%.2f | Current: $%.2f | Progress: %.1f%%",
                goalName, targetAmount, currentSavings, getProgressPercentage());
    }
}