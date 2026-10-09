
package com.siya.controller;

import com.siya.entity.LoginHistory;
import com.siya.entity.User;
import com.siya.repository.LoginHistoryRepository;
import com.siya.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(
    origins = {
        "http://localhost:5500",
        "http://127.0.0.1:5500",
        "https://siyajadhav31.github.io"
    },
    allowCredentials = "true"
)
public class UserAuthController {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UserAuthController(
            UserRepository userRepository,
            LoginHistoryRepository loginHistoryRepository) {

        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
    }

    // ==========================================
    // CUSTOMER REGISTRATION
    // POST /api/users/register
    // ==========================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Map<String, String> data) {

        String name = data.get("name");
        String email = data.get("email");
        String password = data.get("password");

        if (name == null || name.isBlank()
                || email == null || email.isBlank()
                || password == null || password.length() < 8) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "Name and email are required. Password must be at least 8 characters."
                )
            );
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of(
                    "message",
                    "This email is already registered."
                )
            );
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(normalizedEmail);

        // Store hashed password, not plain text
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        return ResponseEntity.ok(
            Map.of("message", "Registration successful.")
        );
    }

    // ==========================================
    // CUSTOMER LOGIN
    // POST /api/users/login
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> data) {

        String email = data.get("email");
        String password = data.get("password");

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {

            return ResponseEntity.badRequest().body(
                Map.of(
                    "message",
                    "Email and password are required."
                )
            );
        }

        User user = userRepository
                .findByEmailIgnoreCase(email.trim())
                .orElse(null);

        if (user == null
                || !passwordEncoder.matches(
                        password, user.getPassword())) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        Map.of(
                            "message",
                            "Incorrect email or password."
                        )
                    );
        }

        LocalDateTime now = LocalDateTime.now();

        user.setLastLogin(now);
        userRepository.save(user);

        // Save every successful login
        loginHistoryRepository.save(
            new LoginHistory(user, now)
        );

        return ResponseEntity.ok(
            Map.of(
                "message", "Login successful.",
                "name", user.getName(),
                "email", user.getEmail()
            )
        );
    }

    // ==========================================
    // ADMIN SESSION CHECK
    // ==========================================

    private boolean isAdminLoggedIn(HttpSession session) {

        return Boolean.TRUE.equals(
            session.getAttribute("adminLoggedIn")
        );
    }

    // ==========================================
    // GET ALL REGISTERED USERS
    // GET /api/users/all
    // ==========================================

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        Map.of(
                            "message",
                            "Admin login required."
                        )
                    );
        }

        List<Map<String, Object>> users =
                userRepository.findAll()
                    .stream()
                    .map(user -> {
                        Map<String, Object> item =
                                new java.util.LinkedHashMap<>();

                        item.put("id", user.getId());
                        item.put("name", user.getName());
                        item.put("email", user.getEmail());

                        item.put(
                            "createdAt",
                            user.getCreatedAt() == null
                                ? ""
                                : user.getCreatedAt().toString()
                        );

                        item.put(
                            "lastLogin",
                            user.getLastLogin() == null
                                ? ""
                                : user.getLastLogin().toString()
                        );

                        return item;
                    })
                    .toList();

        return ResponseEntity.ok(users);
    }

    // ==========================================
    // GET CUSTOMER LOGIN HISTORY
    // GET /api/users/login-history
    // ==========================================

    @GetMapping("/login-history")
    public ResponseEntity<?> getLoginHistory(
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        Map.of(
                            "message",
                            "Admin login required."
                        )
                    );
        }

        List<Map<String, Object>> history =
                loginHistoryRepository
                    .findAllByOrderByLoginTimeDesc()
                    .stream()
                    .map(record -> {
                        Map<String, Object> item =
                                new java.util.LinkedHashMap<>();

                        item.put("id", record.getId());
                        item.put(
                            "name",
                            record.getUser().getName()
                        );
                        item.put(
                            "email",
                            record.getUser().getEmail()
                        );
                        item.put(
                            "loginTime",
                            record.getLoginTime().toString()
                        );

                        return item;
                    })
                    .toList();

        return ResponseEntity.ok(history);
    }

    // ==========================================
    // DELETE REGISTERED USER
    // DELETE /api/users/{id}
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            HttpSession session) {

        if (!isAdminLoggedIn(session)) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        Map.of(
                            "message",
                            "Admin login required."
                        )
                    );
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                        Map.of(
                            "message",
                            "User not found."
                        )
                    );
        }

        // Delete login history before deleting the user
        List<LoginHistory> history =
                loginHistoryRepository.findAll()
                    .stream()
                    .filter(record ->
                        record.getUser().getId().equals(id))
                    .toList();

        loginHistoryRepository.deleteAll(history);
        userRepository.delete(user);

        return ResponseEntity.ok(
            Map.of("message", "User deleted successfully.")
        );
    }
}