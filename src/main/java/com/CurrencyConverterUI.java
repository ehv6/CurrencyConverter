package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class CurrencyConverterUI {

    private CurrencyService currencyService;

    public CurrencyConverterUI(CurrencyService currencyService) {
        this.currencyService = currencyService;
        createAndShowUI();
    }

    private void createAndShowUI() {
        // Create the main window (JFrame)
        JFrame frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        
        // Create a JPanel to hold the UI elements
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2)); // Grid layout with 5 rows, 2 columns

        // Create labels and text fields for input
        JLabel fromLabel = new JLabel("Convert from (e.g., USD):");
        JTextField fromCurrencyField = new JTextField();
        
        JLabel toLabel = new JLabel("Convert to (e.g., EUR):");
        JTextField toCurrencyField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        // Create a button to trigger the conversion
        JButton convertButton = new JButton("Convert");

        // Create an output label for the result
        JLabel resultLabel = new JLabel("Result:");

        // Add all components to the panel
        panel.add(fromLabel);
        panel.add(fromCurrencyField);
        panel.add(toLabel);
        panel.add(toCurrencyField);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(new JLabel()); // Empty space
        panel.add(convertButton);
        panel.add(resultLabel);

        // Add panel to the frame
        frame.add(panel);

        // Action listener for the convert button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromCurrency = fromCurrencyField.getText().toUpperCase();
                String toCurrency = toCurrencyField.getText().toUpperCase();
                try {
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    // Call the CurrencyService to perform the conversion
                    BigDecimal convertedAmount = currencyService.convertCurrency(fromCurrency, toCurrency, amount);
                    resultLabel.setText("Result: " + amount + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency);
                } catch (Exception ex) {
                    resultLabel.setText("Error: Invalid input or conversion failed.");
                }
            }
        });

        // Display the window
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        CurrencyService currencyService = new CurrencyService();
        new CurrencyConverterUI(currencyService);
    }
}
