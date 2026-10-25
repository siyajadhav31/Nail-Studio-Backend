
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
            @Value("${resend.from-email:onboarding@resend.dev}")
            String fromEmail) {

        this.fromEmail = fromEmail;

        if (apiKey == null || apiKey.trim().isEmpty()) {
            this.resend = null;
            System.err.println(
                "EMAIL ERROR: RESEND_API_KEY is missing."
            );
        } else {
            this.resend = new Resend(apiKey.trim());
            System.out.println("Resend email service initialized.");
        }

        System.out.println("Resend sender configured: " + fromEmail);
    }

    // =========================================
    // APPOINTMENT CANCELLATION EMAIL
    // =========================================

    public boolean sendCancellationEmail(Booking booking) {

        if (booking == null) {
            System.err.println(
                "CANCELLATION EMAIL FAILED: Booking is null."
            );
            return false;
        }

        String subject = "Nail Studio - Booking Cancelled";

        StringBuilder body = new StringBuilder();

        body.append("Hello ")
            .append(safe(booking.getName()))
            .append(",\n\n");

        body.append(
            "Your Nail Studio appointment has been cancelled.\n\n"
        );

        body.append("Booking ID: ")
            .append(booking.getId())
            .append("\n");

        body.append("Service: ")
            .append(safe(booking.getService()))
            .append("\n");

        body.append("Appointment Date: ")
            .append(safe(booking.getBookingDate()))
            .append("\n");

        body.append("Appointment Time: ")
            .append(safe(booking.getBookingTime()))
            .append("\n\n");

        String refundStatus = safe(booking.getRefundStatus());

        if ("PENDING".equalsIgnoreCase(refundStatus)) {

            body.append(
                "Your refund is pending processing.\n"
            );

            body.append(
                "The studio will update you when the refund status changes.\n\n"
            );

        } else if ("REFUNDED".equalsIgnoreCase(refundStatus)) {

            body.append(
                "The studio has updated your refund status to REFUNDED.\n"
            );

            body.append(
                "Please note that this status update alone "
                + "does not transfer money.\n\n"
            );

        } else {

            body.append("Refund Status: ")
                .append(
                    refundStatus.isEmpty()
                        ? "Not applicable"
                        : refundStatus
                )
                .append("\n\n");
        }

        body.append(
            "If you have any questions, please contact Nail Studio.\n\n"
        );

        body.append("Thank you,\nNail Studio");

        return sendEmail(
            booking.getEmail(),
            subject,
            body.toString()
        );
    }

    // =========================================
    // REFUND STATUS EMAIL
    // =========================================

    public boolean sendRefundEmail(Booking booking) {

        if (booking == null) {
            System.err.println(
                "REFUND EMAIL FAILED: Booking is null."
            );
            return false;
        }

        String subject = "Nail Studio - Refund Status Updated";

        String refundStatus = safe(booking.getRefundStatus());

        StringBuilder body = new StringBuilder();

        body.append("Hello ")
            .append(safe(booking.getName()))
            .append(",\n\n");

        body.append(
            "The refund status for your Nail Studio booking has been updated.\n\n"
        );

        body.append("Booking ID: ")
            .append(booking.getId())
            .append("\n");

        body.append("Service: ")
            .append(safe(booking.getService()))
            .append("\n");

        body.append("Refund Status: ")
            .append(
                refundStatus.isEmpty()
                    ? "Not updated"
                    : refundStatus
            )
            .append("\n\n");

        if ("REFUNDED".equalsIgnoreCase(refundStatus)) {

            body.append(
                "The studio has marked your refund as processed.\n"
            );

            body.append(
                "Please note that this is a status notification only; "
                + "it does not itself transfer money.\n\n"
            );

        } else if ("PENDING".equalsIgnoreCase(refundStatus)) {

            body.append(
                "Your refund is still pending processing.\n\n"
            );
        }

        body.append(
            "If you have any questions, please contact Nail Studio.\n\n"
        );

        body.append("Thank you,\nNail Studio");

        return sendEmail(
            booking.getEmail(),
            subject,
            body.toString()
        );
    }

    // =========================================
    // COMMON RESEND EMAIL METHOD
    // =========================================

    private boolean sendEmail(
            String to,
            String subject,
            String body) {

        if (resend == null) {
            System.err.println(
                "EMAIL FAILED: Resend client is not initialized. "
                + "Check the RESEND_API_KEY environment variable."
            );
            return false;
        }

        if (to == null || to.trim().isEmpty()) {
            System.err.println(
                "EMAIL FAILED: Customer email address is missing."
            );
            return false;
        }

        try {

            CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(to.trim())
                .subject(subject)
                .text(body)
                .build();

            CreateEmailResponse response =
                resend.emails().send(params);

            if (response == null || response.getId() == null) {

                System.err.println(
                    "EMAIL FAILED: Resend returned no email ID."
                );

                return false;
            }

            System.out.println(
                "RESEND EMAIL ACCEPTED"
                + " | To: " + to.trim()
                + " | Subject: " + subject
                + " | Email ID: " + response.getId()
            );

            // This confirms that Resend accepted the request,
            // not that the email reached the customer's inbox.
            return true;

        } catch (ResendException e) {

            System.err.println(
                "RESEND EMAIL ERROR: " + e.getMessage()
            );

            return false;

        } catch (Exception e) {

            System.err.println(
                "EMAIL ERROR: " + e.getClass().getSimpleName()
                + " - " + e.getMessage()
            );

            return false;
        }
    }

    // =========================================
    // NULL-SAFE STRING HELPER
    // =========================================

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
