package com.swabhiman.shiftswap.domain.model;

import com.swabhiman.shiftswap.domain.enums.CertificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff; // You will create 'Staff.java' next

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CertificationType type;

    @Column(nullable = false)
    private LocalDate issuedDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    private String certificateNumber;

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    public boolean isExpiringSoon() {
        return LocalDate.now().plusMonths(1).isAfter(expiryDate);
    }
}