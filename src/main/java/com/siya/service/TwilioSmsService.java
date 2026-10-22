
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
            String accountSid = System.getenv("TWILIO_ACCOUNT_SID");
            String authToken = System.getenv("TWILIO_AUTH_TOKEN");
            String fromNumber = System.getenv("TWILIO_FROM_NUMBER");

            if (accountSid == null || authToken == null
                    || fromNumber == null
                    || accountSid.isBlank()
                    || authToken.isBlank()
                    || fromNumber.isBlank()) {
                System.err.println("Twilio environment variables are missing.");
                return false;
            }

            String toNumber = normalizeIndianPhone(phone);

            if (toNumber == null) {
                System.err.println("Invalid customer phone number.");
                return false;
            }

            String formData =
                    "To=" + encode(toNumber)
                    + "&From=" + encode(fromNumber)
                    + "&Body=" + encode(message);

            String credentials = accountSid + ":" + authToken;
            String basicAuth = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8));

            String url = "https://api.twilio.com/2010-04-01/Accounts/"
                    + accountSid + "/Messages.json";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + basicAuth)
                    .header("Content-Type",
                            "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formData))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200
                    && response.statusCode() < 300) {
                System.out.println("SMS sent successfully.");
                return true;
            }

            System.err.println("Twilio SMS failed. HTTP status: "
                    + response.statusCode());
            System.err.println("Twilio response: " + response.body());
            return false;

        } catch (Exception e) {
            System.err.println("Unable to send SMS: " + e.getMessage());
            return false;
        }
    }

    private String normalizeIndianPhone(String phone) {
        if (phone == null) {
            return null;
        }

        String number = phone.trim().replaceAll("[\\s()-]", "");

        if (number.matches("[6-9][0-9]{9}")) {
            return "+91" + number;
        }

        if (number.matches("\\+\\d{8,15}")) {
            return number;
        }

        return null;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
