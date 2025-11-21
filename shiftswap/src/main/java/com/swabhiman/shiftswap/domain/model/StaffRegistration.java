package com.swabhiman.shiftswap.domain.model;

import java.time.LocalDate;

import com.swabhiman.shiftswap.domain.enums.RegistrationStatus;
import com.swabhiman.shiftswap.domain.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff_registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRegistration extends BaseEntity {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password; // Will be BCrypt hashed

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // RN, LPN, CNA, etc.

    @Column(nullable = false, unique = true)
    private String employeeId;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String jobTitle;

    private Integer yearsOfExperience;

    @Column(length = 1000)
    private String certifications; // Comma-separated list or JSON

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RegistrationStatus status = RegistrationStatus.PENDING_APPROVAL;

    @Column(length = 500)
    private String rejectionReason; // Filled if rejected

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
