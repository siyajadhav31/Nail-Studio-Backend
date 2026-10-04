package com.siya.controller;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;

import jakarta.servlet.http.HttpSession;

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

    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }


    // ==========================================
    // ADMIN LOGIN
    // ==========================================

    @PostMapping("/login")
    public String loginAdmin(
            @RequestBody Admin admin,
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADMIN LOGIN REQUEST");
        System.out.println("Email received: [" + admin.getEmail() + "]");
        System.out.println("Password received: [" + admin.getPassword() + "]");
        System.out.println("=================================");


        // Get all admins from database
        List<Admin> admins = adminRepository.findAll();

        Admin existingAdmin = null;


        // Find admin by email
        for (Admin a : admins) {

            if (a.getEmail() != null
                    && admin.getEmail() != null
                    && a.getEmail().trim()
                        .equalsIgnoreCase(admin.getEmail().trim())) {

                existingAdmin = a;
                break;
            }
        }


        // Admin not found
        if (existingAdmin == null) {

            System.out.println("ADMIN NOT FOUND");

            return "Admin not found";
        }


        System.out.println("ADMIN FOUND");
        System.out.println(
            "Database email: [" + existingAdmin.getEmail() + "]"
        );


        // Check password
        if (existingAdmin.getPassword() == null
                || !existingAdmin.getPassword()
                    .equals(admin.getPassword())) {

            System.out.println("INVALID PASSWORD");

            return "Invalid admin password";
        }


        // Create session
        session.setAttribute(
                "adminEmail",
                existingAdmin.getEmail()
        );

        session.setAttribute(
                "adminLoggedIn",
                true
        );


        System.out.println("ADMIN LOGIN SUCCESS");

        return "Admin login successful";
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
    public String checkAdminSession(
            HttpSession session) {

        Object adminLoggedIn =
                session.getAttribute("adminLoggedIn");

        if (adminLoggedIn != null
                && (Boolean) adminLoggedIn) {

            return "Admin authenticated";
        }

        return "Admin not authenticated";
    }


    // ==========================================
    // ADMIN LOGOUT
    // ==========================================

    @PostMapping("/logout")
    public String logoutAdmin(
            HttpSession session) {

        session.invalidate();

        return "Admin logout successful";
    }

}