
package com.siya.controller;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    // ADMIN LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> loginAdmin(
            @RequestBody Admin admin,
            HttpSession session) {

        if (admin == null
                || admin.getEmail() == null
                || admin.getPassword() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Email and password are required");
        }

        List<Admin> admins = adminRepository.findAll();

        Admin existingAdmin = null;

        for (Admin a : admins) {
            if (a.getEmail() != null
                    && a.getEmail().trim().equalsIgnoreCase(
                            admin.getEmail().trim())) {

                existingAdmin = a;
                break;
            }
        }

        if (existingAdmin == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin email or password");
        }

        /*
         * This comparison preserves your existing password-storage
         * approach. If your database stores BCrypt hashes, use a
         * PasswordEncoder instead of equals().
         */
        if (existingAdmin.getPassword() == null
                || !existingAdmin.getPassword().equals(
                        admin.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin email or password");
        }

        // Create the server-side admin session
        session.setAttribute(
                "adminEmail",
                existingAdmin.getEmail()
        );

        session.setAttribute(
                "adminLoggedIn",
                Boolean.TRUE
        );

        System.out.println("Admin login successful");
        System.out.println("Session ID: " + session.getId());

        return ResponseEntity.ok("Admin login successful");
    }

    // CHECK ADMIN SESSION
    @GetMapping("/check")
    public ResponseEntity<String> checkAdminSession(
            HttpSession session) {

        Object adminLoggedIn =
                session.getAttribute("adminLoggedIn");

        if (Boolean.TRUE.equals(adminLoggedIn)) {
            return ResponseEntity.ok("Admin authenticated");
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body("Admin login required");
    }

    // ADMIN LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<String> logoutAdmin(
            HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok("Admin logout successful");
    }
}