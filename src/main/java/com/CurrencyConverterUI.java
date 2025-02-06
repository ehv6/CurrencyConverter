package com;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverterUI {
    private static final OkHttpClient client = new OkHttpClient();
    private static final String[] CRYPTO_CURRENCIES = {"BTC", "ETH", "BNB", "ADA", "XRP", "USDT"};
    
    // UI Components
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JComboBox<String> fromFiatDropdown;
    private static JComboBox<String> toFiatDropdown;
    private static JComboBox<String> fromCryptoDropdown;
    private static JComboBox<String> toCryptoDropdown;
    private static JTextField amountField;
    private static JLabel resultLabel;
    private static JRadioButton fiatRadioButton;
    private static JRadioButton cryptoRadioButton;
    private static JButton convertButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        // Create main frame
        frame = new JFrame("Currency Converter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with margin
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add components
        addTitle();
        addConversionTypeSelection();
        addCurrencySelectionPanels();
        addAmountPanel();
        addConvertButton();
        addResultLabel();

        // Initialize dropdowns
        populateFiatCurrencyDropdowns();
        populateCryptoDropdowns();

        // Set initial state
        toggleCurrencyPanels(true);

        // Finalize frame setup
        frame.add(mainPanel);
        frame.setSize(400, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void addTitle() {
        JLabel titleLabel = new JLabel("Currency Converter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.drawString(getText(), 2, 22);
                g2.setColor(Color.BLACK);
                g2.drawString(getText(), 0, 20);
                g2.dispose();
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
    }

    private static void addConversionTypeSelection() {
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout());
        
        fiatRadioButton = new JRadioButton("Fiat Currency");
        cryptoRadioButton = new JRadioButton("Cryptocurrency");
        
        ButtonGroup group = new ButtonGroup();
        group.add(fiatRadioButton);
        group.add(cryptoRadioButton);
        
        fiatRadioButton.setSelected(true);
        fiatRadioButton.addActionListener(e -> toggleCurrencyPanels(true));
        cryptoRadioButton.addActionListener(e -> toggleCurrencyPanels(false));
        
        radioPanel.add(fiatRadioButton);
        radioPanel.add(cryptoRadioButton);
        
        mainPanel.add(radioPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    }

    private static void addCurrencySelectionPanels() {
        // Fiat currency panels
        JPanel fiatPanel = new JPanel();
        fiatPanel.setLayout(new GridLayout(2, 2, 10, 10));
        fiatPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        fromFiatDropdown = new JComboBox<>();
        toFiatDropdown = new JComboBox<>();
        
        fiatPanel.add(new JLabel("From (Fiat):"));
        fiatPanel.add(fromFiatDropdown);
        fiatPanel.add(new JLabel("To (Fiat):"));
        fiatPanel.add(toFiatDropdown);
        
        // Crypto currency panels
        JPanel cryptoPanel = new JPanel();
        cryptoPanel.setLayout(new GridLayout(2, 2, 10, 10));
        cryptoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        fromCryptoDropdown = new JComboBox<>();
        toCryptoDropdown = new JComboBox<>();
        
        cryptoPanel.add(new JLabel("From (Crypto):"));
        cryptoPanel.add(fromCryptoDropdown);
        cryptoPanel.add(new JLabel("To (Crypto):"));
        cryptoPanel.add(toCryptoDropdown);
        
        mainPanel.add(fiatPanel);
        mainPanel.add(cryptoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    }

    private static void addAmountPanel() {
        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        amountField = new JTextField(15);
        amountPanel.add(new JLabel("Amount:"));
        amountPanel.add(amountField);
        mainPanel.add(amountPanel);
        mainPanel.add(Box.createVerticalStrut(10));
    }

    private static void addConvertButton() {
        convertButton = new JButton("Convert") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground().darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        convertButton.setUI(new BasicButtonUI());
        convertButton.setBackground(new Color(0, 120, 215));
        convertButton.setForeground(Color.WHITE);
        convertButton.setFocusPainted(false);
        convertButton.setBorder(new EmptyBorder(10, 25, 10, 25));
        convertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        convertButton.addActionListener(e -> performConversion());
        mainPanel.add(convertButton);
        mainPanel.add(Box.createVerticalStrut(10));
    }

    private static void addResultLabel() {
        resultLabel = new JLabel("");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(resultLabel);
    }

    private static void toggleCurrencyPanels(boolean showFiat) {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                Component[] panelComps = panel.getComponents();
                for (Component c : panelComps) {
                    if (c instanceof JComboBox) {
                        if (c == fromFiatDropdown || c == toFiatDropdown) {
                            c.setVisible(showFiat);
                        } else if (c == fromCryptoDropdown || c == toCryptoDropdown) {
                            c.setVisible(!showFiat);
                        }
                    }
                    if (c instanceof JLabel) {
                        String text = ((JLabel) c).getText();
                        if (text.contains("Fiat")) {
                            c.setVisible(showFiat);
                        } else if (text.contains("Crypto")) {
                            c.setVisible(!showFiat);
                        }
                    }
                }
            }
        }
    }

    private static void populateFiatCurrencyDropdowns() {
        try {
            String url = "https://api.frankfurter.app/currencies";
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                ArrayList<String> currencies = new ArrayList<>();
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    currencies.add(keys.next());
                }
                Collections.sort(currencies);

                for (String currency : currencies) {
                    fromFiatDropdown.addItem(currency);
                    toFiatDropdown.addItem(currency);
                }
            }
        } catch (IOException e) {
            showError("Error loading fiat currencies: " + e.getMessage());
        }
    }

    private static void populateCryptoDropdowns() {
        for (String crypto : CRYPTO_CURRENCIES) {
            fromCryptoDropdown.addItem(crypto);
            toCryptoDropdown.addItem(crypto);
        }
    }

    private static void performConversion() {
        try {
            String fromCurrency = fiatRadioButton.isSelected() ? 
                (String) fromFiatDropdown.getSelectedItem() :
                (String) fromCryptoDropdown.getSelectedItem();
            
            String toCurrency = fiatRadioButton.isSelected() ? 
                (String) toFiatDropdown.getSelectedItem() :
                (String) toCryptoDropdown.getSelectedItem();

            if (fromCurrency.equals(toCurrency)) {
                showError("Cannot convert a currency to itself");
                return;
            }

            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                showError("Please enter an amount");
                return;
            }

            BigDecimal amount = new BigDecimal(amountText);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                showError("Amount must be greater than zero");
                return;
            }

            CurrencyService service = new CurrencyService();
            BigDecimal result = service.convertCurrency(fromCurrency, toCurrency, amount);

            if (result != null) {
                showResult(String.format("%s %s = %s %s", 
                    amount.stripTrailingZeros().toPlainString(),
                    fromCurrency,
                    result.stripTrailingZeros().toPlainString(),
                    toCurrency));
            } else {
                showError("Unable to retrieve conversion rate");
            }

        } catch (NumberFormatException e) {
            showError("Invalid amount format");
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    private static void showError(String message) {
        resultLabel.setText("<html><font color='red'>" + message + "</font></html>");
    }

    private static void showResult(String message) {
        resultLabel.setText("<html><b>" + message + "</b></html>");
    }
}