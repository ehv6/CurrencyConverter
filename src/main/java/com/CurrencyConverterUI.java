package com;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterUI {

    private static OkHttpClient client = new OkHttpClient();
    private static JComboBox<String> fromFiatDropdown;
    private static JComboBox<String> toFiatDropdown;
    private static JComboBox<String> fromCryptoDropdown;
    private static JComboBox<String> toCryptoDropdown;
    private static JRadioButton fiatRadioButton;
    private static JRadioButton cryptoRadioButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Currency Converter");
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.setBackground(Color.LIGHT_GRAY);

        // Title
        JLabel titleLabel = new JLabel("Currency Converter");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(titleLabel, gbc);

        // Conversion Type Selection
        ButtonGroup conversionTypeGroup = new ButtonGroup();
        fiatRadioButton = new JRadioButton("Fiat Currency");
        cryptoRadioButton = new JRadioButton("Cryptocurrency");
        conversionTypeGroup.add(fiatRadioButton);
        conversionTypeGroup.add(cryptoRadioButton);
        fiatRadioButton.setSelected(true);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(fiatRadioButton, gbc);
        gbc.gridx = 1;
        panel.add(cryptoRadioButton, gbc);

        // Fiat Currency Dropdowns
        JLabel fromFiatLabel = new JLabel("Convert From (Fiat):");
        fromFiatLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(fromFiatLabel, gbc);

        fromFiatDropdown = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(fromFiatDropdown, gbc);

        JLabel toFiatLabel = new JLabel("Convert To (Fiat):");
        toFiatLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(toFiatLabel, gbc);

        toFiatDropdown = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(toFiatDropdown, gbc);

        // Crypto Currency Dropdowns (initially hidden)
        JLabel fromCryptoLabel = new JLabel("Convert From (Crypto):");
        fromCryptoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        fromCryptoLabel.setVisible(false);
        panel.add(fromCryptoLabel, gbc);

        fromCryptoDropdown = new JComboBox<>(new String[]{"BTC", "ETH", "BNB", "ADA", "XRP"});
        gbc.gridx = 1;
        fromCryptoDropdown.setVisible(false);
        panel.add(fromCryptoDropdown, gbc);

        JLabel toCryptoLabel = new JLabel("Convert To (Crypto):");
        toCryptoLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        toCryptoLabel.setVisible(false);
        panel.add(toCryptoLabel, gbc);

        toCryptoDropdown = new JComboBox<>(new String[]{"BTC", "ETH", "BNB", "ADA", "XRP"});
        gbc.gridx = 1;
        toCryptoDropdown.setVisible(false);
        panel.add(toCryptoDropdown, gbc);

        // Amount input
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(amountLabel, gbc);

        JTextField amountField = new JTextField(10);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        panel.add(amountField, gbc);

        // Convert button and result label
        JButton convertButton = new JButton("Convert");
        convertButton.setFont(new Font("Arial", Font.BOLD, 16));
        convertButton.setBackground(Color.BLUE);
        convertButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        panel.add(convertButton, gbc);

        JLabel resultLabel = new JLabel();
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 8;
        panel.add(resultLabel, gbc);

        // Radio button listeners for switching between fiat and crypto
        fiatRadioButton.addActionListener(e -> {
            fromFiatLabel.setVisible(true);
            fromFiatDropdown.setVisible(true);
            toFiatLabel.setVisible(true);
            toFiatDropdown.setVisible(true);
            fromCryptoLabel.setVisible(false);
            fromCryptoDropdown.setVisible(false);
            toCryptoLabel.setVisible(false);
            toCryptoDropdown.setVisible(false);
        });

        cryptoRadioButton.addActionListener(e -> {
            fromFiatLabel.setVisible(false);
            fromFiatDropdown.setVisible(false);
            toFiatLabel.setVisible(false);
            toFiatDropdown.setVisible(false);
            fromCryptoLabel.setVisible(true);
            fromCryptoDropdown.setVisible(true);
            toCryptoLabel.setVisible(true);
            toCryptoDropdown.setVisible(true);
        });

        // Convert button action
        convertButton.addActionListener(e -> {
            try {
                String fromCurrency, toCurrency;
                if (fiatRadioButton.isSelected()) {
                    fromCurrency = (String) fromFiatDropdown.getSelectedItem();
                    toCurrency = (String) toFiatDropdown.getSelectedItem();
                } else {
                    fromCurrency = (String) fromCryptoDropdown.getSelectedItem();
                    toCurrency = (String) toCryptoDropdown.getSelectedItem();
                }

                BigDecimal amount = new BigDecimal(amountField.getText());
                CurrencyService currencyService = new CurrencyService();
                BigDecimal convertedAmount = currencyService.convertCurrency(fromCurrency, toCurrency, amount);
                
                if (convertedAmount != null) {
                    resultLabel.setText(String.format("<html><b>Result:</b> %s %s = %s %s</html>",
                            amount, fromCurrency, convertedAmount, toCurrency));
                } else {
                    resultLabel.setText("Error: Unable to convert currency.");
                }
            } catch (Exception ex) {
                resultLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Populate fiat currency dropdowns
        populateFiatCurrencyDropdowns();

        // Finalize frame setup
        frame.add(panel);
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Method to populate the fiat currency dropdowns
    private static void populateFiatCurrencyDropdowns() {
        try {
            String url = "https://api.frankfurter.app/currencies";
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);

                ArrayList<String> currencyCodes = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String currencyCode = keys.next();
                    currencyCodes.add(currencyCode);
                }

                Collections.sort(currencyCodes);

                for (String code : currencyCodes) {
                    fromFiatDropdown.addItem(code);
                    toFiatDropdown.addItem(code);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error fetching currencies: " + response.message());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}