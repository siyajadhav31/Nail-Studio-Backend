package com.siya.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.siya.entity.Booking;
import com.siya.repository.BookingRepository;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(
    origins = {
        "https://siyajadhav31.github.io",
        "http://localhost:5500",
        "http://127.0.0.1:5500"
    },
    allowCredentials = "true"
)
public class PaymentController {

    private final RazorpayClient razorpayClient;
    private final BookingRepository bookingRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PaymentController(
            RazorpayClient razorpayClient,
            BookingRepository bookingRepository) {

        this.razorpayClient = razorpayClient;
        this.bookingRepository = bookingRepository;
    }


    // ==========================================
    // CREATE RAZORPAY ORDER
    // ==========================================

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder() {

        try {

            // ==========================================
            // BOOKING ADVANCE
            // ₹500 = 50000 PAISE
            // ==========================================

            int amountInPaise = 50000;


            // ==========================================
            // CREATE ORDER REQUEST
            // ==========================================

            JSONObject orderRequest = new JSONObject();

            orderRequest.put(
                    "amount",
                    amountInPaise
            );

            orderRequest.put(
                    "currency",
                    "INR"
            );

            orderRequest.put(
                    "receipt",
                    "NailStudio_" + System.currentTimeMillis()
            );


            // ==========================================
            // CREATE RAZORPAY ORDER
            // ==========================================

            Order order =
                    razorpayClient.orders.create(
                            orderRequest
                    );


            // ==========================================
            // SEND ORDER DETAILS TO FRONTEND
            // ==========================================

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "orderId",
                    order.get("id")
            );

            response.put(
                    "amount",
                    order.get("amount")
            );

            response.put(
                    "currency",
                    order.get("currency")
            );

            response.put(
                    "keyId",
                    razorpayKeyId
            );


            System.out.println(
                    "================================="
            );

            System.out.println(
                    "💳 RAZORPAY ORDER CREATED"
            );

            System.out.println(
                    "Order ID: " +
                    order.get("id")
            );

            System.out.println(
                    "Amount: ₹500"
            );

            System.out.println(
                    "================================="
            );


            return ResponseEntity.ok(response);

        }

        catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> error =
                    new HashMap<>();

            error.put(
                    "message",
                    "Unable to create payment order"
            );

            error.put(
                    "error",
                    e.getMessage()
            );


            return ResponseEntity
                    .internalServerError()
                    .body(error);
        }
    }


    // ==========================================
    // VERIFY PAYMENT + SAVE BOOKING
    // ==========================================

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(
            @RequestBody Map<String, Object> paymentData) {

        try {

            // ==========================================
            // GET RAZORPAY PAYMENT DETAILS
            // ==========================================

            String razorpayOrderId =
                    String.valueOf(
                            paymentData.get("razorpayOrderId")
                    );

            String razorpayPaymentId =
                    String.valueOf(
                            paymentData.get("razorpayPaymentId")
                    );

            String razorpaySignature =
                    String.valueOf(
                            paymentData.get("razorpaySignature")
                    );


            // ==========================================
            // VERIFY REQUIRED VALUES
            // ==========================================

            if (
                    razorpayOrderId == null ||
                    razorpayPaymentId == null ||
                    razorpaySignature == null
            ) {

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Payment details are missing"
                                )
                        );
            }


            // ==========================================
            // RAZORPAY SIGNATURE VERIFICATION
            // ==========================================

            JSONObject options =
                    new JSONObject();

            options.put(
                    "razorpay_order_id",
                    razorpayOrderId
            );

            options.put(
                    "razorpay_payment_id",
                    razorpayPaymentId
            );

            options.put(
                    "razorpay_signature",
                    razorpaySignature
            );


            boolean paymentVerified =
                    Utils.verifyPaymentSignature(
                            options,
                            razorpayKeySecret
                    );


            // ==========================================
            // PAYMENT FAILED / INVALID
            // ==========================================

            if (!paymentVerified) {

                System.out.println(
                        "❌ PAYMENT VERIFICATION FAILED"
                );

                return ResponseEntity
                        .badRequest()
                        .body(
                                Map.of(
                                        "success",
                                        false,
                                        "message",
                                        "Payment verification failed"
                                )
                        );
            }


            // ==========================================
            // PAYMENT SUCCESSFUL
            // ==========================================

            System.out.println(
                    "================================="
            );

            System.out.println(
                    "✅ PAYMENT VERIFIED"
            );

            System.out.println(
                    "Order ID: " +
                    razorpayOrderId
            );

            System.out.println(
                    "Payment ID: " +
                    razorpayPaymentId
            );

            System.out.println(
                    "================================="
            );


            // ==========================================
            // CREATE BOOKING OBJECT
            // ==========================================

            Booking booking =
                    new Booking();


            booking.setName(
                    getValue(
                            paymentData,
                            "name"
                    )
            );

            booking.setEmail(
                    getValue(
                            paymentData,
                            "email"
                    )
            );

            booking.setPhone(
                    getValue(
                            paymentData,
                            "phone"
                    )
            );

            booking.setService(
                    getValue(
                            paymentData,
                            "service"
                    )
            );

            booking.setDesign(
                    getValue(
                            paymentData,
                            "design"
                    )
            );

            booking.setShape(
                    getValue(
                            paymentData,
                            "shape"
                    )
            );

            booking.setShade(
                    getValue(
                            paymentData,
                            "shade"
                    )
            );

            booking.setBookingDate(
                    getValue(
                            paymentData,
                            "bookingDate"
                    )
            );

            booking.setBookingTime(
                    getValue(
                            paymentData,
                            "bookingTime"
                    )
            );

            booking.setBookingType(
                    "SERVICE"
            );

            booking.setNotes(
                    getValue(
                            paymentData,
                            "notes"
                    )
            );


            // ==========================================
            // PAYMENT INFORMATION
            // ==========================================

            booking.setPaymentStatus(
                    "PAID"
            );

            booking.setPaymentId(
                    razorpayPaymentId
            );

            booking.setRazorpayOrderId(
                    razorpayOrderId
            );

            booking.setPaymentAmount(
                    500.0
            );


            // ==========================================
            // SAVE BOOKING IN DATABASE
            // ==========================================

            Booking savedBooking =
                    bookingRepository.save(
                            booking
                    );


            System.out.println(
                    "================================="
            );

            System.out.println(
                    "💅 BOOKING SAVED AFTER PAYMENT"
            );

            System.out.println(
                    "Booking ID: " +
                    savedBooking.getId()
            );

            System.out.println(
                    "Payment Status: PAID"
            );

            System.out.println(
                    "================================="
            );


            // ==========================================
            // RESPONSE
            // ==========================================

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "success",
                    true
            );

            response.put(
                    "message",
                    "Payment successful and booking confirmed"
            );

            response.put(
                    "bookingId",
                    savedBooking.getId()
            );

            response.put(
                    "paymentId",
                    razorpayPaymentId
            );

            response.put(
                    "orderId",
                    razorpayOrderId
            );

            response.put(
                    "paymentStatus",
                    "PAID"
            );


            return ResponseEntity.ok(
                    response
            );

        }

        catch (Exception e) {

            e.printStackTrace();

            Map<String, Object> error =
                    new HashMap<>();

            error.put(
                    "success",
                    false
            );

            error.put(
                    "message",
                    "Payment verification failed"
            );

            error.put(
                    "error",
                    e.getMessage()
            );


            return ResponseEntity
                    .internalServerError()
                    .body(error);
        }
    }


    // ==========================================
    // HELPER METHOD
    // ==========================================

    private String getValue(
            Map<String, Object> data,
            String key) {

        Object value =
                data.get(key);

        if (value == null) {
            return null;
        }

        return String.valueOf(value).trim();
    }
}