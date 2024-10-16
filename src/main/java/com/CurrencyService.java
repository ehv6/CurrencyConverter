package com;

import java.math.BigDecimal;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyService {
    private final OkHttpClient client = new OkHttpClient();

    public CurrencyRate getCurrencyRate(String convertFrom, String convertTo) {
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
                    return new CurrencyRate(convertTo, rate); // Return a CurrencyRate object
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching conversion rates: " + e.getMessage());
        }
        return null; // Return null if fetching rates fails
    }

    public BigDecimal convertCurrency(String convertFrom, String convertTo, BigDecimal quantity) {
        CurrencyRate currencyRate = getCurrencyRate(convertFrom, convertTo);
        if (currencyRate != null) {
            return currencyRate.convert(quantity); // Use the CurrencyRate to convert the amount
        }
        return null; // Return null if conversion fails
    }
}
