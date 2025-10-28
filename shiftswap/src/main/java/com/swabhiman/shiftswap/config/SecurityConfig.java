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
                // Allow anyone to access the home page, login, and static files
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                // Only users with "STAFF" role can access /staff/** urls
                .requestMatchers("/staff/**").hasRole("STAFF")
                // Only users with "MANAGER" role can access /manager/** urls
                .requestMatchers("/manager/**").hasRole("MANAGER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Any other request must be authenticated (user must be logged in)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // This is your custom login page
                .defaultSuccessUrl("/dashboard", true) // Where to go after login
                .permitAll() // Allow everyone to see the login page
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Show a "logged out" message
                .permitAll()
            );

        return http.build();
    }
}