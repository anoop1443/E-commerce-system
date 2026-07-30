package com.example.homeelecation.ui.razorpay;



import android.os.StrictMode;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A utility class to handle the creation of a Razorpay order.
 * This code is intended to be run on a secure server, not directly within the Android client,
 * to protect your Razorpay Key Secret.
 */
public class RazorpayOrderCreator {

    // --- IMPORTANT ---
    // Replace these with your actual Razorpay Key ID and Key Secret.
    // It's highly recommended to load these from a secure configuration file or environment variables.
    private static final String KEY_ID = "rzp_test_u6RLB7UD85ABD8";
    private static final String KEY_SECRET = "G8m4hCu0LecEf9ArlPLmmt4X";

    private static final String RAZORPAY_API_URL = "https://api.razorpay.com/v1/orders";

    /**
     * Creates a Razorpay order and returns the order ID.
     *
     * @param amount The transaction amount in the smallest currency unit (e.g., for ₹500.00, use 50000).
     * @param currency The currency code (e.g., "INR").
     * @param receiptId A unique identifier for the receipt from your system.
     * @return The generated Razorpay Order ID, or null if an error occurs.
     */
    public static String createRazorpayOrder(int amount, String currency, String receiptId) {

        //networking
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //networking

        // Basic validation
        if (KEY_ID.equals("YOUR_KEY_ID") || KEY_SECRET.equals("YOUR_KEY_SECRET")) {
            System.err.println("Error: Razorpay API Keys are not set. Please replace placeholders.");
            return null;
        }

        HttpURLConnection connection = null;
        String orderId = "0";

        try {
            // 1. Create a JSON object for the order payload
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount); // amount in the smallest currency unit
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", receiptId);

            // 2. Set up the HTTP connection
            URL url = new URL(RAZORPAY_API_URL);
            connection = (HttpURLConnection) url.openConnection();

            // 3. Configure the connection for a POST request
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Accept", "application/json");

            // 4. Set up Basic Authentication
            // This combines your Key ID and Key Secret for authorization.
            String authString = KEY_ID + ":" + KEY_SECRET;
            String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
            connection.setRequestProperty("Authorization", "Basic" + encodedAuth);

            connection.setDoOutput(true);

            // 5. Write the JSON payload to the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = orderRequest.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // 6. Check the HTTP response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                // 7. Read and parse the response
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }

                    // 8. Extract the order ID from the JSON response
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    orderId = jsonResponse.getString("id");
                    System.out.println("Successfully created Razorpay Order ID: " + orderId);
                }
            } else {
                // Handle error response from the server
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    System.err.println("Error creating Razorpay order. Response Code: " + responseCode);
                    System.err.println("Error Response: " + errorResponse.toString());
                }
            }

        } catch (Exception e) {
            // Handle exceptions like network errors or JSON parsing issues
            e.printStackTrace();
            return null;
        } finally {
            // 9. Disconnect the connection
            if (connection != null) {
                connection.disconnect();
            }
        }
//        if (orderId.isEmpty()){
//            startPayment(orderId);
//        }

        return orderId;
    }

    /**
     * Main method for demonstration purposes.
     * In a real Android app, you would call createRazorpayOrder from a backend service.
     */
//    public static void main(String[] args) {
//        // Example usage: Create an order for ₹500.00
//        int amountInPaisa = 500 * 100;
//        String currency = "INR";
//        String receipt = "receipt_#12345";
//
//        System.out.println("Attempting to create a Razorpay order...");
//        String orderId = createRazorpayOrder(amountInPaisa, currency, receipt);
//
//        if (orderId != null) {
//            System.out.println("------------------------------------");
//            System.out.println("Returned Order ID: " + orderId);
//            System.out.println("Next Step: Pass this Order ID to your Android app to open the checkout.");
//            System.out.println("------------------------------------");
//        } else {
//            System.err.println("Failed to create Razorpay order.");
//        }
//    }
}
