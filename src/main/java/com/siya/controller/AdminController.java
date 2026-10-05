package com.siya.controller;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "https://siyajadhav31.github.io"
    },
    allowCredentials = "true"
)
public class AdminController {

    private final AdminRepository adminRepository;


    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public AdminController(AdminRepository adminRepository) {

        this.adminRepository = adminRepository;
    }


    // ==========================================
    // ADMIN LOGIN
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(
            @RequestBody Admin admin,
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADMIN LOGIN REQUEST");
        System.out.println("=================================");

        System.out.println(
            "Email received: [" +
            admin.getEmail() +
            "]"
        );

        System.out.println(
            "Password received: [" +
            admin.getPassword() +
            "]"
        );

        System.out.println("=================================");


        // ------------------------------------------
        // GET ALL ADMINS
        // ------------------------------------------

        List<Admin> admins =
                adminRepository.findAll();


        Admin existingAdmin = null;


        // ------------------------------------------
        // FIND ADMIN BY EMAIL
        // ------------------------------------------

        for (Admin a : admins) {

            if (a.getEmail() != null
                    && admin.getEmail() != null
                    && a.getEmail()
                        .trim()
                        .equalsIgnoreCase(
                            admin.getEmail().trim()
                        )) {

                existingAdmin = a;

                break;
            }
        }


        // ------------------------------------------
        // ADMIN NOT FOUND
        // ------------------------------------------

        if (existingAdmin == null) {

            System.out.println(
                "ADMIN NOT FOUND"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Admin not found");
        }


        // ------------------------------------------
        // ADMIN FOUND
        // ------------------------------------------

        System.out.println(
            "ADMIN FOUND"
        );

        System.out.println(
            "Database email: [" +
            existingAdmin.getEmail() +
            "]"
        );


        // ------------------------------------------
        // CHECK PASSWORD
        // ------------------------------------------

        if (existingAdmin.getPassword() == null
                || admin.getPassword() == null
                || !existingAdmin.getPassword()
                    .equals(admin.getPassword())) {

            System.out.println(
                "INVALID PASSWORD"
            );

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin password");
        }


        // ------------------------------------------
        // CREATE ADMIN SESSION
        // ------------------------------------------

        session.setAttribute(
            "adminEmail",
            existingAdmin.getEmail()
        );

        session.setAttribute(
            "adminLoggedIn",
            true
        );


        // ------------------------------------------
        // DEBUG SESSION
        // ------------------------------------------

        System.out.println(
            "SESSION ID: " +
            session.getId()
        );

        System.out.println(
            "ADMIN EMAIL SESSION: " +
            session.getAttribute("adminEmail")
        );

        System.out.println(
            "ADMIN LOGGED IN SESSION: " +
            session.getAttribute("adminLoggedIn")
        );

        System.out.println(
            "ADMIN LOGIN SUCCESS"
        );

        System.out.println("=================================");


        return ResponseEntity
                .ok("Admin login successful");
    }


    // ==========================================
    // GET ALL ADMINS
    // ==========================================

    @GetMapping("/all")
    public List<Admin> getAllAdmins() {

        return adminRepository.findAll();
    }


    // ==========================================
    // CHECK ADMIN SESSION
    // ==========================================

    @GetMapping("/check")
    public ResponseEntity<String> checkAdminSession(
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADMIN SESSION CHECK");
        System.out.println("Session ID: " + session.getId());


        Object adminLoggedIn =
                session.getAttribute(
                    "adminLoggedIn"
                );


        Object adminEmail =
                session.getAttribute(
                    "adminEmail"
                );


        System.out.println(
            "adminLoggedIn: " +
            adminLoggedIn
        );

        System.out.println(
            "adminEmail: " +
            adminEmail
        );


        // ------------------------------------------
        // SESSION VALID
        // ------------------------------------------

        if (Boolean.TRUE.equals(adminLoggedIn)) {

            System.out.println(
                "ADMIN AUTHENTICATED"
            );

            System.out.println("=================================");

            return ResponseEntity
                    .ok("Admin authenticated");
        }


        // ------------------------------------------
        // SESSION INVALID
        // ------------------------------------------

        System.out.println(
            "ADMIN NOT AUTHENTICATED"
        );

        System.out.println("=================================");


        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Admin not authenticated");
    }


    // ==========================================
    // ADMIN LOGOUT
    // ==========================================

    @PostMapping("/logout")
    public ResponseEntity<String> logoutAdmin(
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADMIN LOGOUT");
        System.out.println(
            "Session ID: " +
            session.getId()
        );


        session.invalidate();


        System.out.println(
            "ADMIN LOGOUT SUCCESS"
        );

        System.out.println("=================================");


        return ResponseEntity
                .ok("Admin logout successful");
    }
}