package com.siya.controller;

import com.siya.entity.Booking;
import com.siya.repository.BookingRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingRepository bookingRepository;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public BookingController(BookingRepository bookingRepository) {

        this.bookingRepository = bookingRepository;
    }


    // ==========================================
    // CREATE BOOKING
    // ==========================================

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {

        // If booking type is not provided,
        // consider it as normal service booking.

        if (booking.getBookingType() == null ||
            booking.getBookingType().trim().isEmpty()) {

            booking.setBookingType("SERVICE");
        }

        return bookingRepository.save(booking);
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
    public Booking getBookingById(@PathVariable Long id) {

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


        existingBooking.setDesign(
                updatedBooking.getDesign()
        );


        existingBooking.setBookingDate(
                updatedBooking.getBookingDate()
        );


        existingBooking.setBookingTime(
                updatedBooking.getBookingTime()
        );


        // Update booking type only if provided

        if (updatedBooking.getBookingType() != null &&
            !updatedBooking.getBookingType().trim().isEmpty()) {

            existingBooking.setBookingType(
                    updatedBooking.getBookingType()
            );
        }


        return bookingRepository.save(existingBooking);
    }


    // ==========================================
    // DELETE BOOKING
    // ==========================================

    @DeleteMapping("/{id}")
    public String deleteBooking(@PathVariable Long id) {

        if (!bookingRepository.existsById(id)) {

            return "Booking not found";
        }


        bookingRepository.deleteById(id);


        return "Booking deleted successfully";
    }
}