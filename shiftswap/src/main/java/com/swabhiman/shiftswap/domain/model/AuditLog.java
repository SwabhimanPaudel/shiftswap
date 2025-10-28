package com.swabhiman.shiftswap.domain.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "swap_id", nullable = false)
    private Swap swap;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;   // Null if it was a system action

    @Column(nullable = false)
    private String action;   // e.g., "POSTED", "CLAIMED", "APPROVED"

    @Column(nullable = false)
    private Instant timestamp;

    private String reasonCode;   // e.g., "OVERTIME_RISK"

    @Column(length = 1000)
    private String reasonDetail;

    @Column(columnDefinition = "jsonb") // This is a PostgreSQL-specific type for JSON
    private String metadata;   // Stored as JSON for flexible data

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    // This annotation prevents an audit log from ever being changed
    @PreUpdate
    private void preventUpdate() {
        throw new IllegalStateException("Audit logs cannot be modified");
    }
}