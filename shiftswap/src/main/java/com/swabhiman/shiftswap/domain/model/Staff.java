package com.swabhiman.shiftswap.domain.model;

import com.swabhiman.shiftswap.domain.enums.CertificationType; // You created this
import com.swabhiman.shiftswap.domain.enums.Role; // You created this
import com.swabhiman.shiftswap.domain.enums.Unit; // You created this
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // Links to the User.java you created

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility; // Links to the Facility.java you created

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Unit primaryUnit;

    @Column(nullable = false)
    private int maxWeeklyHours = 60;

    @Column(nullable = false)
    private double reliabilityScore = 100.0;   // 0-100

    private String phoneNumber;

    // This links to the Certification.java you just made
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Certification> certifications = new HashSet<>();

    // Helper method
    public boolean hasCertification(CertificationType type) {
        return certifications.stream()
            .anyMatch(cert -> cert.getType() == type && !cert.isExpired());
    }
}