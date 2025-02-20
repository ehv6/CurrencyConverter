package com.Backend;

import java.math.BigDecimal;
import java.util.Date;

public class ConversionHistory {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal amount;
    private BigDecimal result;
    private Date timestamp;

    public ConversionHistory(String from, String to, BigDecimal amount, BigDecimal result) {
        this.fromCurrency = from;
        this.toCurrency = to;
        this.amount = amount;
        this.result = result;
        this.timestamp = new Date();
    }
}