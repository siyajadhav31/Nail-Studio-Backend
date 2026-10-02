package com.siya.controller;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(
    origins = "http://localhost:5500",
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

        Admin existingAdmin = adminRepository
                .findByEmail(admin.getEmail())
                .orElse(null);

        if (existingAdmin == null) {
            return "Admin not found";
        }

        if (!existingAdmin.getPassword().equals(admin.getPassword())) {
            return "Invalid admin password";
        }

        // Store admin information in session
        session.setAttribute("adminEmail", existingAdmin.getEmail());
        session.setAttribute("adminLoggedIn", true);

        return "Admin login successful";
    }


    @GetMapping("/check")
    public String checkAdminSession(HttpSession session) {

        Object adminLoggedIn =
                session.getAttribute("adminLoggedIn");

        if (adminLoggedIn != null &&
            (Boolean) adminLoggedIn) {

            return "Admin authenticated";
        }

        return "Admin not authenticated";
    }


    @PostMapping("/logout")
    public String logoutAdmin(HttpSession session) {

        session.invalidate();

        return "Admin logout successful";
    }
}