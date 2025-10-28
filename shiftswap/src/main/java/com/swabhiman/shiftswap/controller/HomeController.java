package com.swabhiman.shiftswap.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * This method serves the login page.
     * When Spring Security redirects to "/login", this method catches it
     * and returns the template named "login".
     */
    @GetMapping("/login")
    public String login() {
        return "login"; // This tells Thymeleaf to find "login.html"
    }

    /**
     * This handles the default success URL from your SecurityConfig.
     * After login, it checks your role and sends you to the correct dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login";
        }

        // Check roles and redirect
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            return "redirect:/manager/dashboard"; // You'll build this page later
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
            return "redirect:/staff/dashboard"; // You'll build this page later
        }

        // A fallback
        return "redirect:/login";
    }

    /**
     * This handles the root URL "/"
     */
    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/dashboard"; // If logged in, go to dashboard
        }
        return "redirect:/login"; // If not, go to login
    }
}