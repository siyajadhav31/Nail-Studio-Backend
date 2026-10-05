package com.siya.config;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminDataInitializer {

    @Bean
    public CommandLineRunner createDefaultAdmin(
            AdminRepository adminRepository) {

        return args -> {

<<<<<<< HEAD
            System.out.println("=================================");
            System.out.println("ADMIN INITIALIZER RUNNING");
            System.out.println("=================================");

            if (adminRepository.findByEmail("admin@gmail.com").isEmpty()) {
=======
            System.out.println(
                "================================="
            );

            System.out.println(
                "ADMIN INITIALIZER RUNNING"
            );

            System.out.println(
                "================================="
            );


            // ------------------------------------------
            // CHECK ADMIN
            // ------------------------------------------

            if (
                adminRepository
                    .findByEmail("admin@gmail.com")
                    .isEmpty()
            ) {
>>>>>>> be2f73b (booking)

                Admin admin = new Admin();


                // ------------------------------------------
                // DEFAULT ADMIN
                // ------------------------------------------

                admin.setEmail(
                    "admin@gmail.com"
                );

                admin.setPassword(
                    "admin123"
                );


                // ------------------------------------------
                // SAVE ADMIN
                // ------------------------------------------

                adminRepository.save(admin);

<<<<<<< HEAD
                System.out.println("=================================");
                System.out.println("DEFAULT ADMIN CREATED");
                System.out.println("Email: admin@gmail.com");
                System.out.println("Password: admin123");
                System.out.println("=================================");

            } else {

                System.out.println("=================================");
                System.out.println("DEFAULT ADMIN ALREADY EXISTS");
                System.out.println("=================================");
=======

                System.out.println(
                    "================================="
                );

                System.out.println(
                    "DEFAULT ADMIN CREATED"
                );

                System.out.println(
                    "Email: admin@gmail.com"
                );

                System.out.println(
                    "Password: admin123"
                );

                System.out.println(
                    "================================="
                );

            } else {

                System.out.println(
                    "================================="
                );

                System.out.println(
                    "DEFAULT ADMIN ALREADY EXISTS"
                );

                System.out.println(
                    "================================="
                );
>>>>>>> be2f73b (booking)
            }
        };
    }
}
