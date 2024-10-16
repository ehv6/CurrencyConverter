package com;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        panel.setLayout(new GridLayout(4, 2));

        // Label and Dropdowns for currency selection
        JLabel fromCurrencyLabel = new JLabel("Convert From:");
        fromCurrencyDropdown = new JComboBox<>();
        JLabel toCurrencyLabel = new JLabel("Convert To:");
        toCurrencyDropdown = new JComboBox<>();

        // Fetch and populate the dropdown with currency codes from API
        populateCurrencyDropdowns();

        // Amount input
        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField();

        // Convert button
        JButton convertButton = new JButton("Convert");
        JLabel resultLabel = new JLabel();

        // Add components to the panel
        panel.add(fromCurrencyLabel);
        panel.add(fromCurrencyDropdown);
        panel.add(toCurrencyLabel);
        panel.add(toCurrencyDropdown);
        panel.add(amountLabel);
        panel.add(amountField);
        panel.add(convertButton);
        panel.add(resultLabel);

        // Add panel to the frame
        frame.add(panel);
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Action for convert button
        convertButton.addActionListener(e -> {
            try {
                String fromCurrency = (String) fromCurrencyDropdown.getSelectedItem();
                String toCurrency = (String) toCurrencyDropdown.getSelectedItem();
                BigDecimal amount = new BigDecimal(amountField.getText());
                BigDecimal convertedAmount = convertCurrency(fromCurrency, toCurrency, amount);
                resultLabel.setText(amount + " " + fromCurrency + " = " + convertedAmount + " " + toCurrency);
            } catch (Exception ex) {
                resultLabel.setText("Error: " + ex.getMessage());
            }
        });
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

                // Iterate through the JSON object and add each currency code to the dropdown
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String currencyCode = keys.next();
                    fromCurrencyDropdown.addItem(currencyCode);
                    toCurrencyDropdown.addItem(currencyCode);
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
