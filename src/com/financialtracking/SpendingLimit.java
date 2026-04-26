package com.financialtracking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class SpendingLimit implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM");

    private double limitAmount;
    private double currentSpending;
    private YearMonth monthYear;

    public SpendingLimit(double limitAmount) throws FinancialException {
        if (limitAmount <= 0) {
            throw new FinancialException("Spending limit must be positive");
        }

        this.limitAmount = limitAmount;
        this.currentSpending = 0.0;
        this.monthYear = YearMonth.now();
    }

    /**
     * Add spending to current month
     */
    public void addSpending(double amount) throws FinancialException {
        if (amount <= 0) {
            throw new FinancialException("Spending amount must be positive");
        }
        this.currentSpending += amount;
    }

    /**
     * Get spending percentage
     */
    public double getSpendingPercentage() {
        if (limitAmount == 0) {
            return 0;
        }
        return (currentSpending / limitAmount) * 100;
    }

    /**
     * Check if at 80% threshold
     */
    public boolean isAt80Percent() {
        return getSpendingPercentage() >= 80 && getSpendingPercentage() < 100;
    }

    /**
     * Check if at or exceeded limit
     */
    public boolean isAtOrExceededLimit() {
        return getSpendingPercentage() >= 100;
    }

    /**
     * Get remaining budget
     */
    public double getRemainingBudget() {
        return Math.max(0, limitAmount - currentSpending);
    }

    /**
     * Get overage amount
     */
    public double getOverageAmount() {
        return Math.max(0, currentSpending - limitAmount);
    }

    /**
     * Check if current month has changed and reset if needed
     */
    public void checkAndResetIfNewMonth() throws FinancialException {
        YearMonth currentMonth = YearMonth.now();
        if (!currentMonth.equals(monthYear)) {
            this.monthYear = currentMonth;
            this.currentSpending = 0.0;
        }
    }

    /**
     * Format for file storage
     */
    public String toFileFormat() {
        return String.format("%.2f|%.2f|%s",
                limitAmount, currentSpending, monthYear.format(formatter));
    }

    /**
     * Parse from file format
     */
    public static SpendingLimit fromFileFormat(String line) throws FinancialException {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 3) {
                throw new FinancialException("Invalid spending limit format");
            }

            double limitAmount = Double.parseDouble(parts[0]);
            double currentSpending = Double.parseDouble(parts[1]);
            YearMonth monthYear = YearMonth.parse(parts[2], formatter);

            SpendingLimit limit = new SpendingLimit(limitAmount);
            limit.currentSpending = currentSpending;
            limit.monthYear = monthYear;

            return limit;
        } catch (Exception e) {
            throw new FinancialException("Error parsing spending limit: " + e.getMessage(), e);
        }
    }

    // Getters and Setters
    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) throws FinancialException {
        if (limitAmount <= 0) {
            throw new FinancialException("Spending limit must be positive");
        }
        this.limitAmount = limitAmount;
    }

    public double getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(double currentSpending) {
        this.currentSpending = currentSpending;
    }

    public YearMonth getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(YearMonth monthYear) {
        this.monthYear = monthYear;
    }

    @Override
    public String toString() {
        return String.format("Limit: $%.2f | Current: $%.2f | Remaining: $%.2f | Progress: %.1f%%",
                limitAmount, currentSpending, getRemainingBudget(), getSpendingPercentage());
    }
}