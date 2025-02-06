package com;

import java.math.BigDecimal;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyService {
    private final OkHttpClient client = new OkHttpClient();
    private static final String COINGECKO_SIMPLE_PRICE = "https://api.coingecko.com/api/v3/simple/price";
    private static final String FRANKFURTER_BASE = "https://api.frankfurter.app";

    public CurrencyRate getCurrencyRate(String convertFrom, String convertTo) {
        if (isFiatCurrency(convertFrom) && isFiatCurrency(convertTo)) {
            return getFiatCurrencyRate(convertFrom, convertTo);
        } else if (isCryptoCurrency(convertFrom) || isCryptoCurrency(convertTo)) {
            return getCryptoRate(convertFrom, convertTo);
        }
        return null;
    }

    private CurrencyRate getCryptoRate(String convertFrom, String convertTo) {
        try {
            // Map currency codes to CoinGecko IDs
            String fromId = getCoinGeckoId(convertFrom.toLowerCase());
            String toSymbol = convertTo.toLowerCase();

            String url = String.format("%s?ids=%s&vs_currencies=%s", 
                COINGECKO_SIMPLE_PRICE, fromId, toSymbol);

            Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) return null;

            JSONObject data = new JSONObject(response.body().string());
            if (data.has(fromId)) {
                JSONObject rates = data.getJSONObject(fromId);
                if (rates.has(toSymbol)) {
                    BigDecimal rate = BigDecimal.valueOf(rates.getDouble(toSymbol));
                    return new CurrencyRate(convertTo, rate);
                }
            }
        } catch (Exception e) {
            logError("Crypto conversion error", e);
        }
        return null;
    }

    private String getCoinGeckoId(String symbol) {
        // Map common symbols to CoinGecko IDs
        switch (symbol) {
            case "btc": return "bitcoin";
            case "eth": return "ethereum";
            case "bnb": return "binancecoin";
            case "ada": return "cardano";
            case "xrp": return "ripple";
            case "usdt": return "tether";
            default: return symbol;
        }
    }

    private CurrencyRate getFiatCurrencyRate(String convertFrom, String convertTo) {
        try {
            String url = FRANKFURTER_BASE + "/latest?base=" + convertFrom + "&symbols=" + convertTo;
            Request request = new Request.Builder().url(url).get().build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                if (jsonObject.has("rates")) {
                    BigDecimal rate = jsonObject.getJSONObject("rates").getBigDecimal(convertTo);
                    return new CurrencyRate(convertTo, rate);
                }
            }
        } catch (Exception e) {
            logError("Fiat conversion error", e);
        }
        return null;
    }

    public BigDecimal convertCurrency(String convertFrom, String convertTo, BigDecimal quantity) {
        CurrencyRate rate = getCurrencyRate(convertFrom, convertTo);
        if (rate != null) {
            return rate.convert(quantity);
        }
        return null;
    }

    private boolean isFiatCurrency(String currency) {
        return currency.matches("^[A-Z]{3}$") && !isCryptoCurrency(currency);
    }

    private boolean isCryptoCurrency(String currency) {
        return currency.matches("^(BTC|ETH|BNB|XRP|ADA|USDT)$");
    }

    private void logError(String message, Exception e) {
        System.err.println(message);
        System.err.println("Error details: " + e.getMessage());
        e.printStackTrace();
    }
}