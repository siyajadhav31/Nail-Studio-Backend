package com.siya.controller;

import com.siya.entity.Booking;
import com.siya.repository.BookingRepository;

import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(
    origins = {
        "https://siyajadhav31.github.io",
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "http://localhost:5501",
        "http://127.0.0.1:5501"
    },
    allowCredentials = "true"
)
public class PaymentController {

    private final BookingRepository bookingRepository;

    public PaymentController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // =========================================================
    // COMPLETE ONLINE PAYMENT
    // =========================================================

    @PostMapping("/complete")
    public Booking completePayment(
            @RequestBody Map<String, Object> paymentData) {

        System.out.println("=================================");
        System.out.println("💳 ONLINE PAYMENT REQUEST");

        // =====================================================
        // GET BOOKING ID
        // =====================================================

        Object bookingIdObject = paymentData.get("bookingId");

        if (bookingIdObject == null) {
            throw new RuntimeException("Booking ID is required");
        }

        Long bookingId = Long.valueOf(
            bookingIdObject.toString()
        );

        System.out.println("Booking ID: " + bookingId);

        // =====================================================
        // FIND BOOKING
        // =====================================================

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElse(null);

        if (booking == null) {
            throw new RuntimeException("Booking not found");
        }

        // =====================================================
        // CHECK BOOKING TYPE
        // =====================================================

        if (!"SERVICE".equalsIgnoreCase(
                booking.getBookingType())) {

            throw new RuntimeException(
                "Online payment is available only for service bookings"
            );
        }

        // =====================================================
        // CHECK PAYMENT AMOUNT
        // =====================================================

        if (booking.getPaymentAmount() == null ||
            booking.getPaymentAmount() <= 0) {

            throw new RuntimeException(
                "Invalid payment amount"
            );
        }

        // =====================================================
        // GENERATE PAYMENT ID
        // =====================================================

        String datePart = new SimpleDateFormat(
            "yyyyMMddHHmmss"
        ).format(new Date());

        String randomPart = UUID
                .randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        String paymentId =
                "PAY-" +
                datePart +
                "-" +
                randomPart;

        // =====================================================
        // UPDATE PAYMENT DETAILS
        // =====================================================

        booking.setPaymentMethod("UPI");
        booking.setPaymentStatus("PAID");
        booking.setPaymentId(paymentId);

        // =====================================================
        // SAVE
        // =====================================================

        Booking savedBooking =
                bookingRepository.save(booking);

        // =====================================================
        // LOG
        // =====================================================

        System.out.println("=================================");
        System.out.println("✅ PAYMENT SUCCESS");
        System.out.println(
            "Booking ID: " +
            savedBooking.getId()
        );

        System.out.println(
            "Payment ID: " +
            savedBooking.getPaymentId()
        );

        System.out.println(
            "Amount: ₹" +
            savedBooking.getPaymentAmount()
        );

        System.out.println(
            "Payment Method: " +
            savedBooking.getPaymentMethod()
        );

        System.out.println(
            "Payment Status: " +
            savedBooking.getPaymentStatus()
        );

        System.out.println("=================================");

        return savedBooking;
    }


    // =========================================================
    // OLD VERIFY ENDPOINT
    // =========================================================
    // Existing frontend agar /verify call kare to bhi kaam karega.
    // =========================================================

    @PostMapping("/verify")
    public Booking verifyPayment(
            @RequestBody Map<String, Object> paymentData) {

        System.out.println("=================================");
        System.out.println("💳 PAYMENT VERIFICATION");

        Object bookingIdObject =
                paymentData.get("bookingId");

        if (bookingIdObject == null) {
            throw new RuntimeException(
                "Booking ID is required"
            );
        }

        Long bookingId =
                Long.valueOf(
                    bookingIdObject.toString()
                );

        // =====================================================
        // FIND BOOKING
        // =====================================================

        Booking booking =
                bookingRepository
                    .findById(bookingId)
                    .orElse(null);

        if (booking == null) {
            throw new RuntimeException(
                "Booking not found"
            );
        }

        // =====================================================
        // CHECK STATUS
        // =====================================================

        Object paymentStatusObject =
                paymentData.get("paymentStatus");

        String paymentStatus =
                paymentStatusObject == null
                    ? "SUCCESS"
                    : paymentStatusObject
                        .toString()
                        .trim()
                        .toUpperCase();

        // =====================================================
        // PAYMENT ID
        // =====================================================

        Object paymentIdObject =
                paymentData.get("paymentId");

        String paymentId;

        if (paymentIdObject != null &&
            !paymentIdObject.toString().trim().isEmpty()) {

            paymentId =
                paymentIdObject
                    .toString()
                    .trim();

        } else {

            String datePart =
                    new SimpleDateFormat(
                        "yyyyMMddHHmmss"
                    ).format(new Date());

            String randomPart =
                    UUID
                        .randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

            paymentId =
                    "PAY-" +
                    datePart +
                    "-" +
                    randomPart;
        }

        // =====================================================
        // SUCCESS
        // =====================================================

        if ("SUCCESS".equals(paymentStatus)) {

            booking.setPaymentStatus("PAID");
            booking.setPaymentMethod("UPI");
            booking.setPaymentId(paymentId);

        } else {

            booking.setPaymentStatus("FAILED");
            booking.setPaymentId(paymentId);
        }

        // =====================================================
        // SAVE
        // =====================================================

        Booking savedBooking =
                bookingRepository.save(booking);

        System.out.println(
            "Payment ID: " +
            savedBooking.getPaymentId()
        );

        System.out.println(
            "Payment Status: " +
            savedBooking.getPaymentStatus()
        );

        System.out.println("=================================");

        return savedBooking;
    }
}