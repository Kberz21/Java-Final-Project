package com.financialtracking;

/**
 * Custom exception for financial operation errors
 */
public class FinancialException extends Exception {
    public FinancialException(String message) {
        super(message);
    }

    public FinancialException(String message, Throwable cause) {
        super(message, cause);
    }
}