package com.siya.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    // =====================================================
    // USER DETAILS MANAGER
    // Prevent Spring Security from creating default user
    // =====================================================

    @Bean
    public UserDetailsManager userDetailsManager() {
        return new InMemoryUserDetailsManager();
    }


    // =====================================================
    // SECURITY FILTER CHAIN
    // =====================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // =================================================
            // CSRF
            // =================================================

            .csrf(csrf -> csrf.disable())


            // =================================================
            // CORS
            // =================================================

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))


            // =================================================
            // AUTHORIZATION
            // =================================================

            .authorizeHttpRequests(auth -> auth

                // -----------------------------
                // ADMIN APIs
                // -----------------------------

                .requestMatchers(
                    "/api/admin/login",
                    "/api/admin/check",
                    "/api/admin/logout"
                ).permitAll()


                // -----------------------------
                // BOOKING APIs
                // -----------------------------

                .requestMatchers(
                    "/api/bookings/**"
                ).permitAll()


                // -----------------------------
                // ALL OTHER API
                // -----------------------------

                .requestMatchers(
                    "/api/**"
                ).permitAll()


                // -----------------------------
                // OTHER REQUESTS
                // -----------------------------

                .anyRequest().permitAll()
            )


            // =================================================
            // DISABLE SPRING DEFAULT LOGIN
            // =================================================

            .formLogin(form -> form.disable())

            .httpBasic(basic -> basic.disable());


        return http.build();
    }


    // =====================================================
    // CORS CONFIGURATION
    // =====================================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        // =================================================
        // ALLOWED FRONTEND ORIGINS
        // =================================================

        configuration.setAllowedOrigins(
            Arrays.asList(
                "http://localhost:5500",
                "http://127.0.0.1:5500",
                "https://siyajadhav31.github.io"
            )
        );


        // =================================================
        // ALLOWED HTTP METHODS
        // =================================================

        configuration.setAllowedMethods(
            Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );


        // =================================================
        // ALLOWED HEADERS
        // =================================================

        configuration.setAllowedHeaders(
            Arrays.asList("*")
        );


        // =================================================
        // ALLOW SESSION COOKIES
        // =================================================

        configuration.setAllowCredentials(true);


        // =================================================
        // REGISTER CORS
        // =================================================

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}