package com.siya.controller;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public String loginAdmin(
            @RequestBody Admin admin,
            HttpSession session) {

        System.out.println("=================================");
        System.out.println("ADMIN LOGIN REQUEST");
        System.out.println("Email received: [" + admin.getEmail() + "]");
        System.out.println("Password received: [" + admin.getPassword() + "]");
        System.out.println("=================================");

        Admin existingAdmin = adminRepository
                .findByEmail(admin.getEmail())
                .orElse(null);

        if (existingAdmin == null) {

            System.out.println("ADMIN NOT FOUND");

            return "Admin not found";
        }

        System.out.println("ADMIN FOUND");
        System.out.println("Database email: [" + existingAdmin.getEmail() + "]");

        if (!existingAdmin.getPassword()
                .equals(admin.getPassword())) {

            System.out.println("INVALID PASSWORD");

            return "Invalid admin password";
        }

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


    @PostMapping("/logout")
    public String logoutAdmin(
            HttpSession session) {

        session.invalidate();

        return "Admin logout successful";
    }
}