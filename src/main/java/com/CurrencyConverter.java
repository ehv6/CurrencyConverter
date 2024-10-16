package com;

import java.math.BigDecimal;
import java.util.Scanner;

public class CurrencyConverter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // User input for currency conversion
            String convertFrom = getCurrencyInput(scanner, "Enter the currency to convert from (e.g., USD):");
            String convertTo = getCurrencyInput(scanner, "Enter the currency to convert to (e.g., EUR):");
            BigDecimal quantity = getAmountInput(scanner, "Enter the amount to convert:");

            CurrencyService currencyService = new CurrencyService();
            BigDecimal convertedAmount = currencyService.convertCurrency(convertFrom, convertTo, quantity);

            if (convertedAmount != null) {
                System.out.println(quantity + " " + convertFrom + " = " + convertedAmount + " " + convertTo);
            } else {
                System.out.println("Error: Unable to retrieve currency conversion rates.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            scanner.close(); // Ensure the scanner is closed
        }
    }

    private static String getCurrencyInput(Scanner scanner, String prompt) {
        System.out.println(prompt);
        return scanner.nextLine().toUpperCase();
    }

    private static BigDecimal getAmountInput(Scanner scanner, String prompt) {
        System.out.println(prompt);
        return scanner.nextBigDecimal();
    }
}
