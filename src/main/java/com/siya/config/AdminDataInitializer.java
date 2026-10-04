package com.siya.config;

import com.siya.entity.Admin;
import com.siya.repository.AdminRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner createDefaultAdmin(
            AdminRepository adminRepository) {

        return args -> {

            if (adminRepository.findByEmail("admin@gmail.com").isEmpty()) {

                Admin admin = new Admin();

                admin.setEmail("admin@gmail.com");
                admin.setPassword("admin123");

                adminRepository.save(admin);

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
                    "DEFAULT ADMIN ALREADY EXISTS"
                );

            }
        };
    }
}