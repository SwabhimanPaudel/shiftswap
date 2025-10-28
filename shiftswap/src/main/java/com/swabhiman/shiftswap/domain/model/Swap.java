package com.swabhiman.shiftswap.domain.model;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "swaps")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Swap extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "shift_id", nullable = false)
    private Shift shift; // The shift being swapped

    @ManyToOne
    @JoinColumn(name = "original_owner_id", nullable = false)
    private Staff originalOwner; // The person who posted the swap

    @ManyToOne
    @JoinColumn(name = "claimer_id")
    private Staff claimer; // The person who claimed the swap

    @ManyToOne
    @JoinColumn(name = "approving_manager_id")
    private User approvingManager;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwapStatus status = SwapStatus.POSTED; // Uses the enum you created

    @Column(length = 500)
    private String reason;   // Why the original owner wants to swap

    private Instant postedAt;
    private Instant claimedAt;
    private Instant ownerApprovedAt;
    private Instant managerApprovedAt;

    private Instant expiresAt;   // Auto-expire if not claimed

    @Column(length = 500)
    private String rejectionReason;

    // --- Helper Methods ---

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean canBeClaimed() {
        return status == SwapStatus.POSTED && !isExpired();
    }

    public boolean requiresOwnerApproval() {
        return status == SwapStatus.CLAIMED;
    }

    public boolean requiresManagerApproval() {
        return status == SwapStatus.OWNER_APPROVED;
    }

    public long getHoursUntilExpiry() {
        if (expiresAt == null) return Long.MAX_VALUE;
        return Duration.between(Instant.now(), expiresAt).toHours();
    }
}