
package com.siya.controller;

import com.siya.entity.Booking;
import com.siya.repository.BookingRepository;
import com.siya.service.EmailService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
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
public class BookingController {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingController(
            BookingRepository bookingRepository,
            EmailService emailService) {

        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    // =========================================================
    // SERVICE PRICE
    // =========================================================

    private double getServicePrice(String service) {

        if (service == null) {
            return 0.0;
        }

        String serviceName = service.trim();

        switch (serviceName) {

            // NORMAL
            case "Basic Gel Polish":
                return 500.0;
            case "Acrylic Overlay (Natural Nails)":
                return 800.0;
            case "Basic Acrylic Extensions":
                return 1200.0;
            case "Machine Printed Press-On Nails":
                return 300.0;
            case "Cut Polish & Basic Nail Art":
                return 400.0;
            case "Basic French Manicure":
                return 700.0;

            // PREMIUM
            case "Soft Gel Extensions":
                return 2000.0;
            case "BIAB (Builder Gel) Extensions":
                return 2200.0;
            case "Ombre Gel Extensions":
                return 2500.0;
            case "Chrome Finish Gel Extensions":
                return 2200.0;
            case "Cat-Eye Gel Nails":
                return 2400.0;
            case "Custom Handmade Press-Ons":
                return 1200.0;
            case "Rubber Base Gel Overlay":
                return 1800.0;
            case "Matte Finish Gel Extensions":
                return 2000.0;
            case "Glitter Inbuilt Acrylic Extensions":
                return 2500.0;
            case "Minimalist Hand-Painted Art Extensions":
                return 2500.0;

            // LUXURY
            case "Polygel Sculpted Extensions":
                return 3500.0;
            case "Hard Gel Extensions":
                return 3800.0;
            case "3D Encapsulated Art Extensions":
                return 4500.0;
            case "Swarovski & Crystal Studded Set":
                return 5000.0;
            case "Bridal Luxury Customized Set":
                return 6000.0;
            case "Glass/Clear Jelly Extensions with Foil":
                return 4000.0;
            case "Russian Dry Manicure + Polygel Set":
                return 4500.0;
            case "Metallic 3D Sculpted Charms Set":
                return 5000.0;
            case "Hand-Sculpted Floral 3D Art Extensions":
                return 4800.0;

            // SERVICES PAGE
            case "Acrylic Extensions + Gel Polish":
                return 1800.0;
            case "Hands Gel Polish":
                return 699.0;
            case "Bridal Nails":
                return 2499.0;
            case "Temporary Nails + Gel Polish":
                return 1199.0;
            case "Burgundy Nails":
                return 1299.0;
            case "French Nails":
                return 999.0;
            case "Rouge Nails":
                return 1399.0;
            case "Polka Dot Nails":
                return 1099.0;

            default:
                System.out.println(
                    "PRICE NOT FOUND FOR SERVICE: " + serviceName
                );
                return 0.0;
        }
    }

    // =========================================================
    // CREATE BOOKING
    // POST /api/bookings
    // =========================================================

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {

        System.out.println("========== NEW BOOKING ==========");
        System.out.println("Name: " + booking.getName());
        System.out.println("Service: " + booking.getService());
        System.out.println("Booking Type: " + booking.getBookingType());
        System.out.println("Payment Method: " + booking.getPaymentMethod());

        // BOOKING TYPE
        if (booking.getBookingType() == null
                || booking.getBookingType().trim().isEmpty()) {

            if (booking.getNotes() != null
                    && booking.getNotes().toLowerCase()
                        .contains("virtual preview")) {

                booking.setBookingType("VIRTUAL");

            } else {
                booking.setBookingType("SERVICE");
            }

        } else {
            booking.setBookingType(
                booking.getBookingType().trim().toUpperCase()
            );
        }

        // PAYMENT METHOD
        if (booking.getPaymentMethod() == null
                || booking.getPaymentMethod().trim().isEmpty()) {

            booking.setPaymentMethod("CASH");

        } else {
            booking.setPaymentMethod(
                booking.getPaymentMethod().trim().toUpperCase()
            );
        }

        // PAYMENT STATUS AND AMOUNT
        if ("VIRTUAL".equals(booking.getBookingType())) {

            booking.setPaymentStatus("N/A");
            booking.setPaymentAmount(0.0);

        } else {

            booking.setPaymentStatus("PENDING");

            if ("SERVICE".equals(booking.getBookingType())) {
                booking.setPaymentAmount(
                    getServicePrice(booking.getService())
                );
            } else {
                booking.setPaymentAmount(0.0);
            }
        }

        // PAYMENT ID
        if (booking.getPaymentId() != null
                && booking.getPaymentId().trim().isEmpty()) {
            booking.setPaymentId(null);
        }

        // INITIAL STATUS
        booking.setBookingStatus("CONFIRMED");
        booking.setRefundStatus("NOT_REQUIRED");

        Booking savedBooking = bookingRepository.save(booking);

        System.out.println("Booking ID: " + savedBooking.getId());
        System.out.println("Amount: " + savedBooking.getPaymentAmount());
        System.out.println("Payment Status: " + savedBooking.getPaymentStatus());
        System.out.println("=================================");

        return savedBooking;
    }

    // =========================================================
    // GET ALL BOOKINGS
    // GET /api/bookings
    // =========================================================

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // =========================================================
    // GET LOGGED-IN CUSTOMER'S BOOKINGS
    // GET /api/bookings/mine
    // =========================================================

    @GetMapping("/mine")
    public ResponseEntity<?> getMyBookings(HttpSession session) {

        String customerEmail =
            (String) session.getAttribute("customerEmail");

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "message",
                    "Please login to view your bookings."
                ));
        }

        List<Booking> myBookings = bookingRepository.findAll()
            .stream()
            .filter(booking ->
                booking.getEmail() != null
                && booking.getEmail().equalsIgnoreCase(customerEmail)
            )
            .collect(Collectors.toList());

        return ResponseEntity.ok(myBookings);
    }

    // =========================================================
    // GET BOOKING BY NUMERIC ID
    // GET /api/bookings/123
    // =========================================================

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {

        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Booking not found."));
        }

        return ResponseEntity.ok(booking);
    }

    // =========================================================
    // CANCEL CUSTOMER'S OWN BOOKING + EMAIL
    // PUT /api/bookings/{id}/cancel
    // =========================================================

    @PutMapping("/{id:\\d+}/cancel")
    public ResponseEntity<?> cancelMyBooking(
            @PathVariable Long id,
            HttpSession session) {

        String customerEmail =
            (String) session.getAttribute("customerEmail");

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                    "message",
                    "Please login before cancelling a booking."
                ));
        }

        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Booking not found."));
        }

        // CUSTOMER OWNERSHIP CHECK
        if (booking.getEmail() == null
                || !booking.getEmail().equalsIgnoreCase(customerEmail)) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                    "message",
                    "You cannot cancel another customer's booking."
                ));
        }

        if ("CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "message",
                    "This booking is already cancelled."
                ));
        }

        // CANCEL BOOKING
        booking.setBookingStatus("CANCELLED");
        booking.setCancelledAt(LocalDateTime.now());

        // REFUND IS MANUAL
        if ("PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            booking.setRefundStatus("PENDING");
        } else {
            booking.setRefundStatus("NOT_REQUIRED");
        }

        Booking savedBooking = bookingRepository.save(booking);

        // SEND CANCELLATION EMAIL
        boolean emailSent =
            emailService.sendCancellationEmail(savedBooking);

        System.out.println("========== CANCELLATION EMAIL ==========");
        System.out.println("Booking ID: " + savedBooking.getId());
        System.out.println("Customer Email: " + savedBooking.getEmail());
        System.out.println("Cancellation email accepted: " + emailSent);
        System.out.println("========================================");

        return ResponseEntity.ok(
            Map.of(
                "message", "Booking cancelled successfully.",
                "booking", savedBooking,
                "refundStatus", savedBooking.getRefundStatus(),
                "emailSent", emailSent
            )
        );
    }

    // =========================================================
    // UPDATE BOOKING
    // PUT /api/bookings/{id}
    // =========================================================

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking) {

        Booking existingBooking = bookingRepository
            .findById(id)
            .orElse(null);

        if (existingBooking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Booking not found."));
        }

        // BASIC DETAILS
        if (updatedBooking.getName() != null) {
            existingBooking.setName(updatedBooking.getName());
        }

        if (updatedBooking.getEmail() != null) {
            existingBooking.setEmail(updatedBooking.getEmail());
        }

        if (updatedBooking.getPhone() != null) {
            existingBooking.setPhone(updatedBooking.getPhone());
        }

        if (updatedBooking.getService() != null) {
            existingBooking.setService(updatedBooking.getService());
        }

        // PAYMENT METHOD
        if (updatedBooking.getPaymentMethod() != null
                && !updatedBooking.getPaymentMethod().trim().isEmpty()) {

            existingBooking.setPaymentMethod(
                updatedBooking.getPaymentMethod().trim().toUpperCase()
            );
        }

        // PAYMENT STATUS
        if (updatedBooking.getPaymentStatus() != null
                && !updatedBooking.getPaymentStatus().trim().isEmpty()) {

            existingBooking.setPaymentStatus(
                updatedBooking.getPaymentStatus().trim().toUpperCase()
            );
        }

        // PAYMENT ID
        if (updatedBooking.getPaymentId() != null) {

            if (updatedBooking.getPaymentId().trim().isEmpty()) {
                existingBooking.setPaymentId(null);
            } else {
                existingBooking.setPaymentId(
                    updatedBooking.getPaymentId().trim()
                );
            }
        }

        // DESIGN
        if (updatedBooking.getDesign() != null) {
            existingBooking.setDesign(updatedBooking.getDesign());
        }

        // SHAPE
        if (updatedBooking.getShape() != null) {
            existingBooking.setShape(updatedBooking.getShape());
        }

        // SHADE
        if (updatedBooking.getShade() != null) {
            existingBooking.setShade(updatedBooking.getShade());
        }

        // NOTES
        if (updatedBooking.getNotes() != null) {
            existingBooking.setNotes(updatedBooking.getNotes());
        }

        // DATE
        if (updatedBooking.getBookingDate() != null
                && !updatedBooking.getBookingDate().trim().isEmpty()) {

            existingBooking.setBookingDate(
                updatedBooking.getBookingDate()
            );
        }

        // TIME
        if (updatedBooking.getBookingTime() != null
                && !updatedBooking.getBookingTime().trim().isEmpty()) {

            existingBooking.setBookingTime(
                updatedBooking.getBookingTime()
            );
        }

        // BOOKING TYPE
        if (updatedBooking.getBookingType() != null
                && !updatedBooking.getBookingType().trim().isEmpty()) {

            existingBooking.setBookingType(
                updatedBooking.getBookingType().trim().toUpperCase()
            );
        }

        // PAYMENT AMOUNT AND DEFAULT STATUS
        if ("SERVICE".equals(existingBooking.getBookingType())) {

            existingBooking.setPaymentAmount(
                getServicePrice(existingBooking.getService())
            );

            if (existingBooking.getPaymentStatus() == null
                    || existingBooking.getPaymentStatus().trim().isEmpty()) {

                existingBooking.setPaymentStatus("PENDING");
            }

        } else if ("OFFER".equals(existingBooking.getBookingType())) {

            existingBooking.setPaymentAmount(0.0);

            if (existingBooking.getPaymentStatus() == null
                    || existingBooking.getPaymentStatus().trim().isEmpty()) {

                existingBooking.setPaymentStatus("PENDING");
            }

        } else if ("VIRTUAL".equals(existingBooking.getBookingType())) {

            existingBooking.setPaymentStatus("N/A");
            existingBooking.setPaymentAmount(0.0);
            existingBooking.setPaymentId(null);
        }

        Booking savedBooking = bookingRepository.save(existingBooking);

        System.out.println("========== BOOKING UPDATED ==========");
        System.out.println("ID: " + savedBooking.getId());
        System.out.println("Service: " + savedBooking.getService());
        System.out.println("Amount: " + savedBooking.getPaymentAmount());
        System.out.println("Payment Method: " + savedBooking.getPaymentMethod());
        System.out.println("Payment Status: " + savedBooking.getPaymentStatus());
        System.out.println("=====================================");

        return ResponseEntity.ok(savedBooking);
    }

    // =========================================================
    // DELETE BOOKING
    // DELETE /api/bookings/{id}
    // =========================================================

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {

        if (!bookingRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Booking not found."));
        }

        bookingRepository.deleteById(id);

        return ResponseEntity.ok(
            Map.of("message", "Booking deleted successfully.")
        );
    }

    // =========================================================
    // ADMIN CONFIRMS MANUALLY PROCESSED REFUND + EMAIL
    // PUT /api/bookings/{id}/confirm-refund
    // =========================================================

    @PutMapping("/{id:\\d+}/confirm-refund")
    public ResponseEntity<?> confirmRefund(
            @PathVariable Long id,
            @RequestParam(required = false) String refundReference,
            HttpSession session) {

        // ADMIN LOGIN CHECK
        if (!Boolean.TRUE.equals(session.getAttribute("adminLoggedIn"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Admin login required."));
        }

        Booking booking = bookingRepository.findById(id).orElse(null);

        if (booking == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Booking not found."));
        }

        if (!"CANCELLED".equalsIgnoreCase(booking.getBookingStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "message",
                    "Only cancelled bookings can be refunded."
                ));
        }

        if (!"PAID".equalsIgnoreCase(booking.getPaymentStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "message",
                    "This booking is not marked as paid."
                ));
        }

        if (!"PENDING".equalsIgnoreCase(booking.getRefundStatus())) {
            return ResponseEntity.badRequest()
                .body(Map.of(
                    "message",
                    "Refund is not pending."
                ));
        }

        // OPTIONAL MANUAL REFUND REFERENCE
        if (refundReference != null && !refundReference.isBlank()) {
            booking.setRefundId(refundReference.trim());
        }

        /*
         * This records the administrator's confirmation that
         * the manual refund has been processed.
         * It does not transfer money automatically.
         */
        booking.setRefundStatus("REFUNDED");

        // SAVE REFUND STATUS
        Booking savedBooking = bookingRepository.save(booking);

        // DEBUG LOGS BEFORE SENDING EMAIL
        System.out.println("========== REFUND EMAIL DEBUG ==========");
        System.out.println("Booking ID: " + savedBooking.getId());
        System.out.println("Customer Email: " + savedBooking.getEmail());
        System.out.println("Booking Status: " + savedBooking.getBookingStatus());
        System.out.println("Payment Status: " + savedBooking.getPaymentStatus());
        System.out.println("Refund Status: " + savedBooking.getRefundStatus());

        // SEND REFUND EMAIL
        boolean emailSent = false;

        try {
            emailSent = emailService.sendRefundEmail(savedBooking);

        } catch (Exception e) {
            System.err.println("REFUND EMAIL EXCEPTION: "
                + e.getClass().getSimpleName()
                + " - " + e.getMessage());
        }

        System.out.println("Refund email accepted: " + emailSent);
        System.out.println("========================================");

        // RETURN RESULT TO FRONTEND
        return ResponseEntity.ok(
            Map.of(
                "message", "Refund status updated.",
                "refundStatus", savedBooking.getRefundStatus(),
                "emailSent", emailSent,
                "booking", savedBooking
            )
        );
    }
}