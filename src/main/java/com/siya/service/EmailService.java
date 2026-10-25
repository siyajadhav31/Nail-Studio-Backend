
package com.siya.service;

import com.siya.entity.Booking;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;

    private final String fromEmail;

    public EmailService(
            @Value("${RESEND_API_KEY:}") String apiKey,
            @Value("${resend.from-email:onboarding@resend.dev}") String fromEmail) {

        this.fromEmail = fromEmail;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            this.resend = null;
            System.err.println(
                "WARNING: RESEND_API_KEY is missing. Email notifications are disabled."
            );
        } else {
            this.resend = new Resend(apiKey.trim());
        }
    }

    // Appointment cancellation email
    public boolean sendCancellationEmail(Booking booking) {

        if (booking == null) {
            System.err.println("EMAIL ERROR: Booking is null.");
            return false;
        }

        String subject = "Nail Studio - Booking Cancelled";

        StringBuilder body = new StringBuilder();

        body.append("Hello ")
            .append(safe(booking.getName()))
            .append(",\n\n");

        body.append("Your Nail Studio appointment has been cancelled.\n");
        body.append("Booking ID: ").append(booking.getId()).append("\n");
        body.append("Service: ").append(safe(booking.getService())).append("\n\n");

        String refundStatus = safe(booking.getRefundStatus());

        if ("PENDING".equalsIgnoreCase(refundStatus)) {
            body.append("Your refund is pending processing.\n\n");
        } else if ("PROCESSED".equalsIgnoreCase(refundStatus)
                || "COMPLETED".equalsIgnoreCase(refundStatus)
                || "REFUNDED".equalsIgnoreCase(refundStatus)) {
            body.append("The studio has updated your refund status to: ")
                .append(refundStatus)
                .append(".\n\n");
            body.append("Please note that this status update does not itself ")
                .append("transfer money.\n\n");
        } else {
            body.append("Refund status: ")
                .append(refundStatus.isEmpty() ? "Not applicable or not updated" : refundStatus)
                .append("\n\n");
        }

        body.append("If you have any questions, please contact Nail Studio.\n\n");
        body.append("Thank you,\nNail Studio");

        return sendEmail(booking.getEmail(), subject, body.toString());
    }

    // Refund status update email
    public boolean sendRefundEmail(Booking booking) {

        if (booking == null) {
            System.err.println("EMAIL ERROR: Booking is null.");
            return false;
        }

        String subject = "Nail Studio - Refund Status Updated";

        String body =
                "Hello " + safe(booking.getName()) + ",\n\n"
                + "Your Nail Studio booking has been cancelled.\n"
                + "Booking ID: " + booking.getId() + "\n\n"
                + "Current refund status: "
                + (safe(booking.getRefundStatus()).isEmpty()
                    ? "Not updated"
                    : safe(booking.getRefundStatus()))
                + "\n\n"
                + "Please note that updating the refund status in the system "
                + "does not itself transfer money. Contact the studio if you "
                + "have questions about your refund.\n\n"
                + "Thank you,\nNail Studio";

        return sendEmail(booking.getEmail(), subject, body);
    }

    // Common email sending method
    private boolean sendEmail(String to, String subject, String body) {

        if (resend == null) {
            System.err.println(
                "EMAIL ERROR: RESEND_API_KEY is missing. Email was not sent."
            );
            return false;
        }

        if (to == null || to.trim().isEmpty()) {
            System.err.println("EMAIL ERROR: Customer email is missing.");
            return false;
        }

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to.trim())
                    .subject(subject)
                    .text(body)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);

            if (response == null || response.getId() == null) {
                System.err.println(
                    "EMAIL ERROR: Resend did not return an email ID."
                );
                return false;
            }

            System.out.println(
                "Resend accepted the email request. Email ID: "
                + response.getId()
            );

            return true;

        } catch (ResendException e) {
            System.err.println("RESEND EMAIL ERROR: " + e.getMessage());
            return false;

        } catch (Exception e) {
            System.err.println("EMAIL ERROR: " + e.getMessage());
            return false;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
