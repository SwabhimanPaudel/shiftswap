package com.swabhiman.shiftswap.domain.model; // This package should be correct

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

// You'll need to create this Enum in a moment
import com.swabhiman.shiftswap.domain.enums.UserRole;

@Entity
@Table(name = "users") // This maps the class to the "users" table in your database
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity implements UserDetails {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;   // This will be BCrypt hashed

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING) // Stores the enum as a string (e.g., "STAFF")
    @Column(nullable = false)
    private UserRole userRole;   // STAFF, MANAGER, ADMIN

    private boolean enabled = true;

    // --- Spring Security required methods ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
    }

    @Override
    public String getUsername() {
        return email; // We are using email as the username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}