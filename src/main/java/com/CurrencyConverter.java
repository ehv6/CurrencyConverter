package com;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

public class CurrencyConverter {

    public static void main(String[] args) throws IOException {
        // Create a Scanner object for user input
        Scanner scanner = new Scanner(System.in);

        // Prompt the user for the currency to convert from
        System.out.println("Type currency to convert from:");
        String convertFrom = scanner.nextLine().toUpperCase();

        // Prompt the user for the currency to convert to
        System.out.println("Type currency to convert to:");
        String convertTo = scanner.nextLine().toUpperCase();

        // Prompt the user for the amount to convert
        System.out.println("Type quantity to convert:");
        BigDecimal quantity = scanner.nextBigDecimal();

        // Create the URL for the API request
        String urlString = "https://www.frankfurter.app/latest?from=" + convertFrom + "&to=" + convertTo;

        // Set up OkHttpClient for the API request
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .get()
                .build();

        // Execute the request and get the response
        Response response = client.newCall(request).execute();
        String stringResponse = response.body().string();

        // Parse the JSON response to get the exchange rate
        JSONObject jsonObject = new JSONObject(stringResponse);
        BigDecimal rate = jsonObject.getJSONObject("rates").getBigDecimal(convertTo);

        // Perform the currency conversion
        BigDecimal result = rate.multiply(quantity);

        // Output the converted result
        System.out.println(result);
    }
}
