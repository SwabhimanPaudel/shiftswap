package com.swabhiman.shiftswap.dto;

import java.time.Instant;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Swap;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SwapResponse {
    private Long id;
    private String unit;
    private String roleRequired;
    private String ownerName;
    private String claimerName;
    private SwapStatus status;
    private Instant postedAt;

    public static SwapResponse from(Swap swap) {
        return new SwapResponse(
            swap.getId(),
            swap.getShift().getUnit().getDisplayName(),
            swap.getShift().getRoleRequired().name(),
            swap.getOriginalOwner().getUser().getFullName(),
            swap.getClaimer() != null ? swap.getClaimer().getUser().getFullName() : null,
            swap.getStatus(),
            swap.getPostedAt()
        );
    }
}


