package com;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterUI {

    private static OkHttpClient client = new OkHttpClient();
    private static JComboBox<String> fromCurrencyDropdown;
    private static JComboBox<String> toCurrencyDropdown;

    public static void main(String[] args) {
        // Create the frame and panel
        JFrame frame = new JFrame("Currency Converter");
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Set font and background color for the panel
        panel.setBackground(Color.LIGHT_GRAY);

        // Create a title label
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        panel.add(titleLabel, gbc);

        // Label and Dropdowns for currency selection
        JLabel fromCurrencyLabel = new JLabel("Convert From:");
        fromCurrencyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridwidth = 1; // Reset to default
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(fromCurrencyLabel, gbc);

        fromCurrencyDropdown = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(fromCurrencyDropdown, gbc);

        JLabel toCurrencyLabel = new JLabel("Convert To:");
        toCurrencyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(toCurrencyLabel, gbc);

        toCurrencyDropdown = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(toCurrencyDropdown, gbc);

        // Amount input
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Convert button
        JButton convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(Color.BLUE);
        convertButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); // Padding
        panel.add(convertButton, gbc);

        JLabel resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 5;
        panel.add(resultLabel, gbc);

        // Add panel to the frame
        frame.add(panel);
        frame.setSize(400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Action for convert button
        convertButton.addActionListener(e -> {
            try {
                String fromCurrency = (String) fromCurrencyDropdown.getSelectedItem();
                String toCurrency = (String) toCurrencyDropdown.getSelectedItem();
                BigDecimal amount = new BigDecimal(amountField.getText());
                BigDecimal convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);
                resultLabel.setText(String.format("<html><b>Result:</b> %s %s = %s %s</html>",
                        amount, fromCurrency, convertedAmount, toCurrency));
            } catch (Exception ex) {
                resultLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Fetch and populate the dropdown with currency codes from API
        populateCurrencyDropdowns();
    }

    // Method to populate the dropdowns with currency codes
    private static void populateCurrencyDropdowns() {
        try {
            // URL to fetch available currencies
            String url = "https://api.frankfurter.app/currencies";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);

                // Store currency codes in a List
                ArrayList<String> currencyCodes = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String currencyCode = keys.next();
                    currencyCodes.add(currencyCode);
                }

                // Sort the currency codes alphabetically
                Collections.sort(currencyCodes);

                // Populate the dropdowns with sorted currency codes
                for (String code : currencyCodes) {
                    fromCurrencyDropdown.addItem(code);
                    toCurrencyDropdown.addItem(code);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error fetching currencies: " + response.message());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Method to handle currency conversion
    private static BigDecimal convertCurrency(String fromCurrency, String toCurrency, BigDecimal amount) throws IOException {
        // URL to fetch the conversion rate
        String url = "https://api.frankfurter.app/latest?base=" + fromCurrency + "&symbols=" + toCurrency;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);

            // Get the conversion rate and calculate the converted amount
            BigDecimal rate = jsonObject.getJSONObject("rates").getBigDecimal(toCurrency);
            return rate.multiply(amount).setScale(4, RoundingMode.HALF_UP);
        } else {
            throw new IOException("Error: " + response.message());
        }
    }
}
