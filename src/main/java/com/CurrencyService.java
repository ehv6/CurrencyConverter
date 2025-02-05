package com;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyService {
    private final OkHttpClient client = new OkHttpClient();
    private static final Map<String, String> CRYPTO_APIS = new HashMap<>() {{
        put("COINgecko", "https://api.coingecko.com/api/v3/simple/price?ids=%s&vs_currencies=%s");
        put("BINANCE", "https://api.binance.com/api/v3/ticker/price?symbol=%s%s");
    }};

    public CurrencyRate getCurrencyRate(String convertFrom, String convertTo) {
        // For traditional currencies, use existing Frankfurter app method
        if (isFiatCurrency(convertFrom) && isFiatCurrency(convertTo)) {
            String urlString = "https://api.frankfurter.app/latest?base=" + convertFrom + "&symbols=" + convertTo;
            Request request = new Request.Builder()
                    .url(urlString)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (jsonObject.has("rates")) {
                        BigDecimal rate = jsonObject.getJSONObject("rates").getBigDecimal(convertTo);
                        return new CurrencyRate(convertTo, rate);
                    }
                }
            } catch (Exception e) {
                System.out.println("Fiat currency error: " + e.getMessage());
            }
        }
        
        // For cryptocurrency conversions
        if (isCryptoCurrency(convertFrom) || isCryptoCurrency(convertTo)) {
            return fetchCryptoCurrencyRate(convertFrom, convertTo);
        }
        
        return null;
    }

    private CurrencyRate fetchCryptoCurrencyRate(String convertFrom, String convertTo) {
        try {
            // CoinGecko API for crypto rates
            String url = String.format(
                CRYPTO_APIS.get("COINgecko"), 
                convertFrom.toLowerCase(), 
                convertTo.toLowerCase()
            );
            
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String stringResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(stringResponse);

                    if (!jsonObject.isEmpty()) {
                        BigDecimal rate = jsonObject
                            .getJSONObject(convertFrom.toLowerCase())
                            .getBigDecimal(convertTo.toLowerCase());
                        
                        return new CurrencyRate(convertTo, rate);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Crypto currency error: " + e.getMessage());
        }
        return null;
    }

    public BigDecimal convertCurrency(String convertFrom, String convertTo, BigDecimal quantity) {
        CurrencyRate currencyRate = getCurrencyRate(convertFrom, convertTo);
        if (currencyRate != null) {
            return currencyRate.convert(quantity);
        }
        return null;
    }

    // Helper methods to identify currency types
    private boolean isFiatCurrency(String currency) {
        // Basic validation for fiat currencies (typically 3 uppercase letters)
        return currency.matches("^[A-Z]{3}$") && !isCryptoCurrency(currency);
    }

    private boolean isCryptoCurrency(String currency) {
        // Add known crypto identifiers
        String[] cryptoPrefixes = {"BTC", "ETH", "XRP", "LTC", "BNB", "ADA"};
        for (String prefix : cryptoPrefixes) {
            if (currency.toUpperCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}