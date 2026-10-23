
package com.siya.service;

import com.siya.entity.Booking;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendCancellationEmail(Booking booking) {

        String subject = "Nail Studio - Booking Cancelled";

        String body = "Hello " + booking.getName() + ",\n\n"
                + "Your Nail Studio appointment has been cancelled.\n"
                + "Booking ID: " + booking.getId() + "\n"
                + "Service: " + booking.getService() + "\n\n";

        if ("PENDING".equalsIgnoreCase(booking.getRefundStatus())) {
            body += "Your refund will be processed within 1 hour. "
                    + "Please note that this is the expected processing time.\n\n";
        } else {
            body += "No refund is pending for this booking.\n\n";
        }

        body += "If you have any questions, please contact Nail Studio.\n\n"
                + "Thank you,\nNail Studio";

        return sendEmail(booking.getEmail(), subject, body);
    }

    public boolean sendRefundEmail(Booking booking) {

        String subject = "Nail Studio - Refund Status Updated";

        String body = "Hello " + booking.getName() + ",\n\n"
                + "Your Nail Studio booking has been cancelled.\n"
                + "Booking ID: " + booking.getId() + "\n\n"
                + "The studio has marked your refund as processed.\n"
                + "Refund status: " + booking.getRefundStatus() + "\n\n"
                + "Please note that this status update does not itself transfer money. "
                + "Contact the studio if you have not received your refund.\n\n"
                + "Thank you,\nNail Studio";

        return sendEmail(booking.getEmail(), subject, body);
    }

    private boolean sendEmail(String to, String subject, String body) {

        if (to == null || to.trim().isEmpty()) {
            System.out.println("EMAIL ERROR: Customer email is missing.");
            return false;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();

            if (fromEmail != null && !fromEmail.trim().isEmpty()) {
                mail.setFrom(fromEmail);
            }

            mail.setTo(to.trim());
            mail.setSubject(subject);
            mail.setText(body);

            mailSender.send(mail);

            System.out.println("Email sent successfully to: " + to);
            return true;

        } catch (Exception e) {
            System.err.println("EMAIL ERROR: " + e.getMessage());
            return false;
        }
    }
}
