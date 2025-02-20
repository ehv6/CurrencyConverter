package com.Backend;


import java.math.BigDecimal;

public class CurrencyRate {
    private String currencyCode;
    private BigDecimal rate;

    // Constructor
    public CurrencyRate(String currencyCode, BigDecimal rate) {
        this.currencyCode = currencyCode;
        this.rate = rate;
    }

    // Getter for currency code
    public String getCurrencyCode() {
        return currencyCode;
    }

    // Getter for rate
    public BigDecimal getRate() {
        return rate;
    }

    // Method to convert an amount using this rate
    public BigDecimal convert(BigDecimal quantity) {
        return rate.multiply(quantity).setScale(4, BigDecimal.ROUND_HALF_UP); // Scale to 4 decimal places
    }

    @Override
    public String toString() {
        return "CurrencyRate{" +
                "currencyCode='" + currencyCode + '\'' +
                ", rate=" + rate +
                '}';
    }
}
