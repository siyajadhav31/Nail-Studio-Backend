
package com.siya.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public boolean sendSms(String phone, String message) {
        try {
            String accountSid =
                    System.getenv("TWILIO_ACCOUNT_SID");

            String authToken =
                    System.getenv("TWILIO_AUTH_TOKEN");

            String fromNumber =
                    System.getenv("TWILIO_FROM_NUMBER");

            if (accountSid == null || accountSid.isBlank()
                    || authToken == null || authToken.isBlank()
                    || fromNumber == null || fromNumber.isBlank()) {

                System.err.println(
                        "SMS ERROR: Twilio environment variables are missing."
                );
                return false;
            }

            String toNumber = normalizeIndianPhone(phone);

            if (toNumber == null) {
                System.err.println(
                        "SMS ERROR: Invalid customer phone number."
                );
                return false;
            }

            String formData =
                    "To=" + encode(toNumber)
                    + "&From=" + encode(fromNumber.trim())
                    + "&Body=" + encode(message);

            String credentials = accountSid.trim()
                    + ":" + authToken.trim();

            String basicAuth = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8)
            );

            String url =
                    "https://api.twilio.com/2010-04-01/Accounts/"
                    + accountSid.trim()
                    + "/Messages.json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + basicAuth)
                    .header(
                            "Content-Type",
                            "application/x-www-form-urlencoded"
                    )
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() >= 200
                    && response.statusCode() < 300) {

                System.out.println(
                        "SMS request accepted by Twilio. HTTP status: "
                        + response.statusCode()
                );

                return true;
            }

            // Twilio response includes an error code and description.
            // Do not print account credentials or the Authorization header.
            System.err.println(
                    "SMS ERROR: Twilio HTTP status: "
                    + response.statusCode()
            );

            System.err.println(
                    "Twilio error response: " + response.body()
            );

            return false;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            System.err.println(
                    "SMS ERROR: Sending SMS was interrupted."
            );

            return false;

        } catch (Exception e) {
            System.err.println(
                    "SMS ERROR: " + e.getMessage()
            );

            return false;
        }
    }

    private String normalizeIndianPhone(String phone) {
        if (phone == null) {
            return null;
        }

        String number = phone.trim()
                .replaceAll("[\\s()-]", "");

        // Indian 10-digit mobile number
        if (number.matches("[6-9][0-9]{9}")) {
            return "+91" + number;
        }

        // International number already in E.164 format
        if (number.matches("\\+[1-9][0-9]{7,14}")) {
            return number;
        }

        return null;
    }

    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}
