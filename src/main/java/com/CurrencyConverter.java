package com;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyConverter {

    public static void main(String[] args) {
        // Create a Scanner object to read user input
        Scanner scanner = new Scanner(System.in);
        try {
            // User input for currency conversion
            System.out.println("Enter the currency to convert from (e.g., USD):");
            String convertFrom = scanner.nextLine().toUpperCase(); // Read and convert input to uppercase
            
            System.out.println("Enter the currency to convert to (e.g., EUR):");
            String convertTo = scanner.nextLine().toUpperCase(); // Read and convert input to uppercase
            
            System.out.println("Enter the amount to convert:");
            BigDecimal quantity = scanner.nextBigDecimal(); // Read the amount as a BigDecimal

            // URL for the API request, using the base and symbols parameters
            String urlString = "https://api.frankfurter.app/latest?base=" + convertFrom + "&symbols=" + convertTo;

            // Set up the OkHttpClient to handle the HTTP request
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(urlString) // Set the request URL
                    .get() // Specify that this is a GET request
                    .build(); // Build the request object

            // Execute the request and get the response
            Response response = client.newCall(request).execute();

            // Check if the response was successful (HTTP status code 200)
            if (response.isSuccessful()) {
                // Parse the response JSON
                String stringResponse = response.body().string(); // Read the response body as a string
                JSONObject jsonObject = new JSONObject(stringResponse); // Create a JSONObject from the string response

                // Check if "rates" exists in the response
                if (jsonObject.has("rates")) {
                    // Retrieve the conversion rate for the specified currency
                    BigDecimal rate = jsonObject.getJSONObject("rates").getBigDecimal(convertTo);

                    // Perform the conversion using the retrieved rate
                    BigDecimal convertedAmount = rate.multiply(quantity).setScale(4, RoundingMode.HALF_UP); // Scale to 4 decimal places

                    // Output the result of the conversion
                    System.out.println(quantity + " " + convertFrom + " = " + convertedAmount + " " + convertTo);
                } else {
                    // Error message if "rates" are not found in the response
                    System.out.println("Error: Conversion rates not found in the response.");
                }
            } else {
                // Print error message if the response was not successful
                System.out.println("Error: " + response.code() + " - " + response.message());
            }
        } catch (Exception e) {
            // Handle any exceptions that occur during execution
            System.out.println("An error occurred: " + e.getMessage());
        } finally {
            // Ensure the scanner is closed to prevent resource leaks
            scanner.close();
        }
    }
}