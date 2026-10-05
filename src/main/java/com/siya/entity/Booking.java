package com.siya.entity;

import com.fasterxml.jackson.annotation.JsonAlias;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String phone;

    private String service;

    private String design;

    private String shape;

    private String shade;

    @JsonAlias({"date"})
    private String bookingDate;

    @JsonAlias({"time"})
    private String bookingTime;

    private String bookingType;

    private String notes;

    // ==========================================
    // PAYMENT METHOD
    // ==========================================

    private String paymentMethod;

    // ==========================================
    // PAYMENT STATUS
    // ==========================================

    private String paymentStatus;

    // ==========================================
    // RAZORPAY PAYMENT ID
    // ==========================================

    private String paymentId;

    // ==========================================
    // RAZORPAY ORDER ID
    // ==========================================

    private String razorpayOrderId;

    // ==========================================
    // PAYMENT AMOUNT
    // ==========================================

    private Double paymentAmount;


    // ==========================================
    // DEFAULT CONSTRUCTOR
    // ==========================================

    public Booking() {

    }


    // ==========================================
    // GET ID
    // ==========================================

    public Long getId() {

        return id;

    }


    // ==========================================
    // NAME
    // ==========================================

    public String getName() {

        return name;

    }

    public void setName(String name) {

        this.name = name;

    }


    // ==========================================
    // EMAIL
    // ==========================================

    public String getEmail() {

        return email;

    }

    public void setEmail(String email) {

        this.email = email;

    }


    // ==========================================
    // PHONE
    // ==========================================

    public String getPhone() {

        return phone;

    }

    public void setPhone(String phone) {

        this.phone = phone;

    }


    // ==========================================
    // SERVICE
    // ==========================================

    public String getService() {

        return service;

    }

    public void setService(String service) {

        this.service = service;

    }


    // ==========================================
    // DESIGN
    // ==========================================

    public String getDesign() {

        return design;

    }

    public void setDesign(String design) {

        this.design = design;

    }


    // ==========================================
    // SHAPE
    // ==========================================

    public String getShape() {

        return shape;

    }

    public void setShape(String shape) {

        this.shape = shape;

    }


    // ==========================================
    // SHADE
    // ==========================================

    public String getShade() {

        return shade;

    }

    public void setShade(String shade) {

        this.shade = shade;

    }


    // ==========================================
    // BOOKING DATE
    // ==========================================

    public String getBookingDate() {

        return bookingDate;

    }

    public void setBookingDate(String bookingDate) {

        this.bookingDate = bookingDate;

    }


    // ==========================================
    // BOOKING TIME
    // ==========================================

    public String getBookingTime() {

        return bookingTime;

    }

    public void setBookingTime(String bookingTime) {

        this.bookingTime = bookingTime;

    }


    // ==========================================
    // BOOKING TYPE
    // ==========================================

    public String getBookingType() {

        return bookingType;

    }

    public void setBookingType(String bookingType) {

        this.bookingType = bookingType;

    }


    // ==========================================
    // NOTES
    // ==========================================

    public String getNotes() {

        return notes;

    }

    public void setNotes(String notes) {

        this.notes = notes;

    }


    // ==========================================
    // PAYMENT METHOD
    // ==========================================

    public String getPaymentMethod() {

        return paymentMethod;

    }

    public void setPaymentMethod(String paymentMethod) {

        this.paymentMethod = paymentMethod;

    }


    // ==========================================
    // PAYMENT STATUS
    // ==========================================

    public String getPaymentStatus() {

        return paymentStatus;

    }

    public void setPaymentStatus(String paymentStatus) {

        this.paymentStatus = paymentStatus;

    }


    // ==========================================
    // PAYMENT ID
    // ==========================================

    public String getPaymentId() {

        return paymentId;

    }

    public void setPaymentId(String paymentId) {

        this.paymentId = paymentId;

    }


    // ==========================================
    // RAZORPAY ORDER ID
    // ==========================================

    public String getRazorpayOrderId() {

        return razorpayOrderId;

    }

    public void setRazorpayOrderId(String razorpayOrderId) {

        this.razorpayOrderId = razorpayOrderId;

    }


    // ==========================================
    // PAYMENT AMOUNT
    // ==========================================

    public Double getPaymentAmount() {

        return paymentAmount;

    }

    public void setPaymentAmount(Double paymentAmount) {

        this.paymentAmount = paymentAmount;

    }

}