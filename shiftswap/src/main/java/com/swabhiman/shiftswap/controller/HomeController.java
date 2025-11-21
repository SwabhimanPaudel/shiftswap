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
            return "redirect:/manager/dashboard";
        }
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STAFF"))) {
            return "redirect:/staff/dashboard";
        }
        // --- THIS BLOCK IS ADDED ---
        // Handle the ADMIN role to prevent the redirect loop
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Send Admins to the manager dashboard (or an admin-specific one if you create
            // it)
            return "redirect:/manager/dashboard";
        }
        // --- END OF ADDED BLOCK ---

        // A fallback
        return "redirect:/login";
    }

    /**
     * This handles the root URL "/"
     */
    @GetMapping("/")
    public String home(Authentication authentication, jakarta.servlet.http.HttpServletResponse response) {
        // Prevent browser from caching this page or the redirect
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        if (authentication != null && authentication.isAuthenticated()) {
            // Only redirect if the user has one of the known roles
            boolean hasRole = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF")
                            || a.getAuthority().equals("ROLE_MANAGER")
                            || a.getAuthority().equals("ROLE_ADMIN"));

            if (hasRole) {
                return "redirect:/dashboard";
            }
        }
        return "landing"; // If not logged in OR logged in but no role, show landing page
    }

    /**
     * This handles the /landing URL
     * Allows both authenticated and unauthenticated users to view the landing page
     */
    @GetMapping("/landing")
    public String landing() {
        return "landing"; // Show landing page regardless of authentication status
    }
}