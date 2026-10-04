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
        "http://127.0.0.1:5500"
    },
    allowCredentials = "true"
)
public class BookingController {

    private final BookingRepository bookingRepository;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public BookingController(
            BookingRepository bookingRepository) {

        this.bookingRepository =
                bookingRepository;
    }


    // ==========================================
    // CREATE BOOKING
    // ==========================================

    @PostMapping
    public Booking createBooking(
            @RequestBody Booking booking) {

        System.out.println(
                "================================="
        );

        System.out.println(
                "💅 NEW BOOKING RECEIVED"
        );

        System.out.println(
                "Name: " + booking.getName()
        );

        System.out.println(
                "Email: " + booking.getEmail()
        );

        System.out.println(
                "Phone: " + booking.getPhone()
        );

        System.out.println(
                "Service: " + booking.getService()
        );

        System.out.println(
                "Booking Date: " +
                booking.getBookingDate()
        );

        System.out.println(
                "Booking Time: " +
                booking.getBookingTime()
        );

        System.out.println(
                "Booking Type: " +
                booking.getBookingType()
        );

        System.out.println(
                "Design: " +
                booking.getDesign()
        );

        System.out.println(
                "Shape: " +
                booking.getShape()
        );

        System.out.println(
                "Shade: " +
                booking.getShade()
        );

        System.out.println(
                "Notes: " +
                booking.getNotes()
        );


        // ==========================================
        // DATE CHECK
        // ==========================================

        if (
            booking.getBookingDate() == null ||
            booking.getBookingDate()
                    .trim()
                    .isEmpty()
        ) {

            System.out.println(
                    "⚠️ Booking date missing"
            );
        }


        // ==========================================
        // TIME CHECK
        // ==========================================

        if (
            booking.getBookingTime() == null ||
            booking.getBookingTime()
                    .trim()
                    .isEmpty()
        ) {

            System.out.println(
                    "⚠️ Booking time missing"
            );
        }


        // ==========================================
        // BOOKING TYPE
        // ==========================================

        if (
            booking.getBookingType() == null ||
            booking.getBookingType()
                    .trim()
                    .isEmpty()
        ) {

            /*
             * Detect old virtual preview booking.
             */

            if (
                booking.getNotes() != null &&
                booking.getNotes()
                        .toLowerCase()
                        .contains("virtual preview")
            ) {

                booking.setBookingType(
                        "VIRTUAL"
                );

            } else {

                booking.setBookingType(
                        "SERVICE"
                );
            }

        } else {

            booking.setBookingType(
                    booking.getBookingType()
                            .trim()
                            .toUpperCase()
            );
        }


        // ==========================================
        // SAVE BOOKING
        // ==========================================

        Booking savedBooking =
                bookingRepository.save(booking);


        System.out.println(
                "================================="
        );

        System.out.println(
                "✅ BOOKING SAVED"
        );

        System.out.println(
                "ID: " +
                savedBooking.getId()
        );

        System.out.println(
                "Type: " +
                savedBooking.getBookingType()
        );

        System.out.println(
                "Date: " +
                savedBooking.getBookingDate()
        );

        System.out.println(
                "Time: " +
                savedBooking.getBookingTime()
        );

        System.out.println(
                "================================="
        );


        return savedBooking;
    }


    // ==========================================
    // GET ALL BOOKINGS
    // ==========================================

    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingRepository.findAll();
    }


    // ==========================================
    // GET BOOKING BY ID
    // ==========================================

    @GetMapping("/{id}")
    public Booking getBookingById(
            @PathVariable Long id) {

        return bookingRepository
                .findById(id)
                .orElse(null);
    }


    // ==========================================
    // UPDATE BOOKING
    // ==========================================

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


        // ==========================================
        // BASIC DETAILS
        // ==========================================

        existingBooking.setName(
                updatedBooking.getName()
        );

        existingBooking.setEmail(
                updatedBooking.getEmail()
        );

        existingBooking.setPhone(
                updatedBooking.getPhone()
        );

        existingBooking.setService(
                updatedBooking.getService()
        );


        // ==========================================
        // VIRTUAL DETAILS
        // ==========================================

        existingBooking.setDesign(
                updatedBooking.getDesign()
        );

        existingBooking.setShape(
                updatedBooking.getShape()
        );

        existingBooking.setShade(
                updatedBooking.getShade()
        );

        existingBooking.setNotes(
                updatedBooking.getNotes()
        );


        // ==========================================
        // DATE
        // ==========================================

        if (
            updatedBooking.getBookingDate() != null
        ) {

            existingBooking.setBookingDate(
                    updatedBooking.getBookingDate()
            );
        }


        // ==========================================
        // TIME
        // ==========================================

        if (
            updatedBooking.getBookingTime() != null
        ) {

            existingBooking.setBookingTime(
                    updatedBooking.getBookingTime()
            );
        }


        // ==========================================
        // BOOKING TYPE
        // ==========================================

        if (
            updatedBooking.getBookingType() != null &&
            !updatedBooking
                    .getBookingType()
                    .trim()
                    .isEmpty()
        ) {

            existingBooking.setBookingType(
                    updatedBooking
                            .getBookingType()
                            .trim()
                            .toUpperCase()
            );
        }


        // ==========================================
        // SAVE UPDATED BOOKING
        // ==========================================

        Booking savedBooking =
                bookingRepository.save(
                        existingBooking
                );


        System.out.println(
                "================================="
        );

        System.out.println(
                "💅 BOOKING UPDATED"
        );

        System.out.println(
                "ID: " +
                savedBooking.getId()
        );

        System.out.println(
                "================================="
        );


        return savedBooking;
    }


    // ==========================================
    // DELETE BOOKING
    // ==========================================

    @DeleteMapping("/{id}")
    public String deleteBooking(
            @PathVariable Long id) {

        System.out.println(
                "================================="
        );

        System.out.println(
                "🗑 DELETE BOOKING REQUEST"
        );

        System.out.println(
                "Booking ID: " + id
        );


        // ==========================================
        // CHECK BOOKING
        // ==========================================

        if (
            !bookingRepository.existsById(id)
        ) {

            System.out.println(
                    "❌ BOOKING NOT FOUND"
            );

            System.out.println(
                    "================================="
            );

            return "Booking not found";
        }


        // ==========================================
        // DELETE
        // ==========================================

        bookingRepository.deleteById(id);


        System.out.println(
                "✅ BOOKING DELETED"
        );

        System.out.println(
                "Deleted ID: " + id
        );

        System.out.println(
                "================================="
        );


        return "Booking deleted successfully";
    }

}