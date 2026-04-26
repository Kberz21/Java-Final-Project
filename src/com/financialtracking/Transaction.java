package com.financialtracking;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static int transactionCounter = 0;

    public enum TransactionType {
        INCOME("Income"),
        EXPENSE("Expense");

        private final String displayName;

        TransactionType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private int id;
    private double amount;
    private TransactionType type;
    private String remark;
    private LocalDateTime timestamp;

    public Transaction(double amount, TransactionType type, String remark) {
        this.id = ++transactionCounter;
        this.amount = amount;
        this.type = type;
        this.remark = remark == null ? "" : remark;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? "" : remark;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Format transaction for file storage
     */
    public String toFileFormat() {
        return String.format("%d|%.2f|%s|%s|%s",
                id, amount, type.name(), timestamp.format(formatter), remark);
    }

    /**
     * Parse transaction from file format
     */
    public static Transaction fromFileFormat(String line) throws FinancialException {
        try {
            String[] parts = line.split("\\|");
            if (parts.length < 4) {
                throw new FinancialException("Invalid transaction format");
            }

            int id = Integer.parseInt(parts[0]);
            double amount = Double.parseDouble(parts[1]);
            TransactionType type = TransactionType.valueOf(parts[2]);
            LocalDateTime timestamp = LocalDateTime.parse(parts[3], formatter);
            String remark = parts.length > 4 ? parts[4] : "";

            Transaction transaction = new Transaction(amount, type, remark);
            transaction.setId(id);
            transaction.setTimestamp(timestamp);
            transactionCounter = Math.max(transactionCounter, id);

            return transaction;
        } catch (Exception e) {
            throw new FinancialException("Error parsing transaction: " + e.getMessage(), e);
        }
    }

    @Override
    public String toString() {
        return String.format("| %3d | %-8s | $%10.2f | %s | %s |",
                id, type.getDisplayName(), amount, 
                timestamp.format(formatter), remark);
    }
}