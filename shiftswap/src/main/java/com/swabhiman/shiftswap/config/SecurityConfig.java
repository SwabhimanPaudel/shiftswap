package com.swabhiman.shiftswap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // This is used to hash passwords
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers("/", "/login", "/landing", "/register", "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        // Only users with "STAFF" role can access /staff/** urls
                        // Only users with "STAFF" role can access /staff/** urls
                        .requestMatchers("/staff/**")
                        .hasAnyAuthority("ROLE_STAFF", "STAFF", "ROLE_MANAGER", "MANAGER", "ROLE_ADMIN", "ADMIN")
                        // Only users with "MANAGER" role can access /manager/** urls
                        .requestMatchers("/manager/**").hasAnyAuthority("ROLE_MANAGER", "MANAGER")
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ADMIN")
                        // Any other request must be authenticated (user must be logged in)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login") // This is your custom login page
                        // FIX 1: Replaced defaultSuccessUrl with a custom successHandler
                        // This correctly redirects users based on their role
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
                                response.sendRedirect("/manager/dashboard");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {
                                response.sendRedirect("/staff/dashboard");
                            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin/dashboard");
                            } else {
                                response.sendRedirect("/dashboard"); // Fallback
                            }
                        })
                        .permitAll() // Allow everyone to see the login page
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout") // Show a "logged out" message
                        .invalidateHttpSession(true) // Invalidate the session
                        .clearAuthentication(true) // Clear the authentication from SecurityContext
                        .deleteCookies("JSESSIONID") // Delete the session cookie
                        .permitAll())
                .sessionManagement(session -> session
                        .sessionFixation().newSession() // Create new session on login to prevent session fixation
                        .maximumSessions(1) // Allow only one session per user
                        .maxSessionsPreventsLogin(false) // New login invalidates old session
                )
                .exceptionHandling(e -> e.accessDeniedHandler((request, response, accessDeniedException) -> {
                    System.out.println("Access Denied Handler caught: " + accessDeniedException.getMessage());
                    System.out.println(
                            "User authorities: " + org.springframework.security.core.context.SecurityContextHolder
                                    .getContext().getAuthentication().getAuthorities());
                    System.out.println("Request URI: " + request.getRequestURI());
                    response.sendRedirect("/error?message=" + accessDeniedException.getMessage());
                }));

        return http.build();
    }
}