package com.financialtracking;

public class Main {
    public static void main(String[] args) {
        try {
            FinancialTrackerApp app = new FinancialTrackerApp();
            app.run();
        } catch (FileOperationException e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}