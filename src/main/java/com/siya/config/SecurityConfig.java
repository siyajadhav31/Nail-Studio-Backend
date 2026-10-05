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

    // ==========================================
    // USER DETAILS MANAGER
    // ==========================================

    @Bean
    public UserDetailsManager userDetailsManager() {

        return new InMemoryUserDetailsManager();
    }


    // ==========================================
    // SECURITY FILTER CHAIN
    // ==========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

            // ------------------------------------------
            // CSRF
            // ------------------------------------------

            .csrf(csrf -> csrf.disable())


            // ------------------------------------------
            // CORS
            // ------------------------------------------

            .cors(cors -> cors.configurationSource(
                    corsConfigurationSource()
            ))


            // ------------------------------------------
            // AUTHORIZATION
            // ------------------------------------------

            .authorizeHttpRequests(auth -> auth

                // Admin login
                .requestMatchers(
                    "/api/admin/login"
                ).permitAll()

                // Admin session check
                .requestMatchers(
                    "/api/admin/check"
                ).permitAll()

                // Admin logout
                .requestMatchers(
                    "/api/admin/logout"
                ).permitAll()

                // All booking APIs
                .requestMatchers(
                    "/api/bookings/**"
                ).permitAll()

                // Other APIs
                .requestMatchers(
                    "/api/**"
                ).permitAll()

                // Everything else
                .anyRequest().permitAll()
            )


            // ------------------------------------------
            // FORM LOGIN DISABLED
            // ------------------------------------------

            .formLogin(form -> form.disable())


            // ------------------------------------------
            // BASIC AUTH DISABLED
            // ------------------------------------------

            .httpBasic(basic -> basic.disable());


        return http.build();
    }


    // ==========================================
    // CORS CONFIGURATION
    // ==========================================

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        // ------------------------------------------
        // ALLOWED FRONTEND ORIGINS
        // ------------------------------------------

        configuration.setAllowedOrigins(
            Arrays.asList(

                "http://localhost:5500",

                "http://127.0.0.1:5500",

                "https://siyajadhav31.github.io"
            )
        );


        // ------------------------------------------
        // ALLOWED METHODS
        // ------------------------------------------

        configuration.setAllowedMethods(
            Arrays.asList(

                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
            )
        );


        // ------------------------------------------
        // ALLOWED HEADERS
        // ------------------------------------------

        configuration.setAllowedHeaders(
            Arrays.asList("*")
        );


        // ------------------------------------------
        // ALLOW COOKIES / SESSION
        // ------------------------------------------

        configuration.setAllowCredentials(true);


        // ------------------------------------------
        // REGISTER CORS
        // ------------------------------------------

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
            "/**",
            configuration
        );


        return source;
    }
}