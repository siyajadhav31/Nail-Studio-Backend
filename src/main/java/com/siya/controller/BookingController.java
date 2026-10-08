package com.siya.controller;

import com.siya.entity.Booking;
import com.siya.repository.BookingRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    public BookingController(
            BookingRepository bookingRepository) {

        this.bookingRepository = bookingRepository;
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

            // =================================================
            // NORMAL
            // =================================================

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


            // =================================================
            // PREMIUM
            // =================================================

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


            // =================================================
            // LUXURY
            // =================================================

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


            // =================================================
            // SERVICES PAGE
            // =================================================

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


            // =================================================
            // UNKNOWN
            // =================================================

            default:
                System.out.println(
                    "⚠️ PRICE NOT FOUND FOR SERVICE: " +
                    serviceName
                );

                return 0.0;
        }
    }


    // =========================================================
    // CREATE BOOKING
    // =========================================================

    @PostMapping
    public Booking createBooking(
            @RequestBody Booking booking) {

        System.out.println("=================================");
        System.out.println("💅 NEW BOOKING");

        System.out.println(
            "Name: " + booking.getName()
        );

        System.out.println(
            "Service: " + booking.getService()
        );

        System.out.println(
            "Booking Type: " +
            booking.getBookingType()
        );

        System.out.println(
            "Payment Method: " +
            booking.getPaymentMethod()
        );


        // =====================================================
        // BOOKING TYPE
        // =====================================================

        if (booking.getBookingType() == null ||
            booking.getBookingType().trim().isEmpty()) {

            if (booking.getNotes() != null &&
                booking.getNotes()
                    .toLowerCase()
                    .contains("virtual preview")) {

                booking.setBookingType("VIRTUAL");

            } else {

                booking.setBookingType("SERVICE");
            }

        } else {

            booking.setBookingType(
                booking.getBookingType()
                    .trim()
                    .toUpperCase()
            );
        }


        // =====================================================
        // PAYMENT METHOD
        // =====================================================

        if (booking.getPaymentMethod() == null ||
            booking.getPaymentMethod().trim().isEmpty()) {

            booking.setPaymentMethod("CASH");

        } else {

            booking.setPaymentMethod(
                booking.getPaymentMethod()
                    .trim()
                    .toUpperCase()
            );
        }


        // =====================================================
        // PAYMENT STATUS
        // =====================================================

        if ("VIRTUAL".equals(
                booking.getBookingType())) {

            booking.setPaymentStatus("N/A");

        } else {

            booking.setPaymentStatus("PENDING");
        }


        // =====================================================
        // PAYMENT AMOUNT
        // =====================================================

        if ("SERVICE".equals(
                booking.getBookingType())) {

            double servicePrice =
                getServicePrice(
                    booking.getService()
                );

            booking.setPaymentAmount(
                servicePrice
            );

        } else {

            booking.setPaymentAmount(0.0);
        }


        // =====================================================
        // PAYMENT ID
        // =====================================================

        // Payment ID payment complete hone ke baad
        // PaymentController generate karega.

        if (booking.getPaymentId() != null &&
            booking.getPaymentId().trim().isEmpty()) {

            booking.setPaymentId(null);
        }


        // =====================================================
        // SAVE
        // =====================================================

        Booking savedBooking =
                bookingRepository.save(booking);


        System.out.println("=================================");
        System.out.println("✅ BOOKING SAVED");

        System.out.println(
            "Booking ID: " +
            savedBooking.getId()
        );

        System.out.println(
            "Service: " +
            savedBooking.getService()
        );

        System.out.println(
            "Amount: ₹" +
            savedBooking.getPaymentAmount()
        );

        System.out.println(
            "Payment Status: " +
            savedBooking.getPaymentStatus()
        );

        System.out.println(
            "Payment ID: " +
            savedBooking.getPaymentId()
        );

        System.out.println("=================================");

        return savedBooking;
    }


    // =========================================================
    // GET ALL BOOKINGS
    // =========================================================

    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    // =========================================================
    // GET BOOKING BY ID
    // =========================================================

    @GetMapping("/{id}")
    public Booking getBookingById(
            @PathVariable Long id) {

        return bookingRepository
                .findById(id)
                .orElse(null);
    }


    // =========================================================
    // UPDATE BOOKING
    // =========================================================

    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking updatedBooking) {

        Booking existingBooking =
                bookingRepository
                    .findById(id)
                    .orElse(null);

        if (existingBooking == null) {
            return null;
        }


        // =====================================================
        // BASIC DETAILS
        // =====================================================

        if (updatedBooking.getName() != null) {
            existingBooking.setName(
                updatedBooking.getName()
            );
        }

        if (updatedBooking.getEmail() != null) {
            existingBooking.setEmail(
                updatedBooking.getEmail()
            );
        }

        if (updatedBooking.getPhone() != null) {
            existingBooking.setPhone(
                updatedBooking.getPhone()
            );
        }

        if (updatedBooking.getService() != null) {
            existingBooking.setService(
                updatedBooking.getService()
            );
        }


        // =====================================================
        // PAYMENT METHOD
        // =====================================================

        if (updatedBooking.getPaymentMethod() != null &&
            !updatedBooking
                .getPaymentMethod()
                .trim()
                .isEmpty()) {

            existingBooking.setPaymentMethod(
                updatedBooking
                    .getPaymentMethod()
                    .trim()
                    .toUpperCase()
            );
        }


        // =====================================================
        // PAYMENT STATUS
        // =====================================================

        if (updatedBooking.getPaymentStatus() != null &&
            !updatedBooking
                .getPaymentStatus()
                .trim()
                .isEmpty()) {

            existingBooking.setPaymentStatus(
                updatedBooking
                    .getPaymentStatus()
                    .trim()
                    .toUpperCase()
            );
        }


        // =====================================================
        // IMPORTANT: PAYMENT ID
        // =====================================================

        if (updatedBooking.getPaymentId() != null &&
            !updatedBooking
                .getPaymentId()
                .trim()
                .isEmpty()) {

            existingBooking.setPaymentId(
                updatedBooking
                    .getPaymentId()
                    .trim()
            );
        }


        // =====================================================
        // DESIGN
        // =====================================================

        if (updatedBooking.getDesign() != null) {
            existingBooking.setDesign(
                updatedBooking.getDesign()
            );
        }


        // =====================================================
        // SHAPE
        // =====================================================

        if (updatedBooking.getShape() != null) {
            existingBooking.setShape(
                updatedBooking.getShape()
            );
        }


        // =====================================================
        // SHADE
        // =====================================================

        if (updatedBooking.getShade() != null) {
            existingBooking.setShade(
                updatedBooking.getShade()
            );
        }


        // =====================================================
        // NOTES
        // =====================================================

        if (updatedBooking.getNotes() != null) {
            existingBooking.setNotes(
                updatedBooking.getNotes()
            );
        }


        // =====================================================
        // DATE
        // =====================================================

        if (updatedBooking.getBookingDate() != null &&
            !updatedBooking
                .getBookingDate()
                .trim()
                .isEmpty()) {

            existingBooking.setBookingDate(
                updatedBooking.getBookingDate()
            );
        }


        // =====================================================
        // TIME
        // =====================================================

        if (updatedBooking.getBookingTime() != null &&
            !updatedBooking
                .getBookingTime()
                .trim()
                .isEmpty()) {

            existingBooking.setBookingTime(
                updatedBooking.getBookingTime()
            );
        }


        // =====================================================
        // BOOKING TYPE
        // =====================================================

        if (updatedBooking.getBookingType() != null &&
            !updatedBooking
                .getBookingType()
                .trim()
                .isEmpty()) {

            existingBooking.setBookingType(
                updatedBooking
                    .getBookingType()
                    .trim()
                    .toUpperCase()
            );
        }


        // =====================================================
        // PAYMENT AMOUNT
        // =====================================================

        if ("SERVICE".equals(
                existingBooking.getBookingType())) {

            existingBooking.setPaymentAmount(
                getServicePrice(
                    existingBooking.getService()
                )
            );

        } else {

            existingBooking.setPaymentAmount(0.0);
        }


        // =====================================================
        // VIRTUAL
        // =====================================================

        if ("VIRTUAL".equals(
                existingBooking.getBookingType())) {

            existingBooking.setPaymentStatus("N/A");
            existingBooking.setPaymentAmount(0.0);
            existingBooking.setPaymentId(null);
        }


        // =====================================================
        // OFFER
        // =====================================================

        else if ("OFFER".equals(
                existingBooking.getBookingType())) {

            existingBooking.setPaymentAmount(0.0);

            if (existingBooking.getPaymentStatus() == null ||
                existingBooking
                    .getPaymentStatus()
                    .trim()
                    .isEmpty()) {

                existingBooking.setPaymentStatus(
                    "PENDING"
                );
            }
        }


        // =====================================================
        // SERVICE
        // =====================================================

        else if ("SERVICE".equals(
                existingBooking.getBookingType())) {

            existingBooking.setPaymentAmount(
                getServicePrice(
                    existingBooking.getService()
                )
            );

            if (existingBooking.getPaymentStatus() == null ||
                existingBooking
                    .getPaymentStatus()
                    .trim()
                    .isEmpty()) {

                existingBooking.setPaymentStatus(
                    "PENDING"
                );
            }
        }


        // =====================================================
        // SAVE
        // =====================================================

        Booking savedBooking =
                bookingRepository.save(
                    existingBooking
                );


        System.out.println("=================================");
        System.out.println("💅 BOOKING UPDATED");

        System.out.println(
            "ID: " +
            savedBooking.getId()
        );

        System.out.println(
            "Service: " +
            savedBooking.getService()
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

        System.out.println(
            "Payment ID: " +
            savedBooking.getPaymentId()
        );

        System.out.println("=================================");

        return savedBooking;
    }


    // =========================================================
    // DELETE BOOKING
    // =========================================================

    @DeleteMapping("/{id}")
    public String deleteBooking(
            @PathVariable Long id) {

        if (!bookingRepository.existsById(id)) {

            return "Booking not found";
        }

        bookingRepository.deleteById(id);

        return "Booking deleted successfully";
    }
}