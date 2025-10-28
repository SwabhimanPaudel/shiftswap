package com.swabhiman.shiftswap.domain.enums;

public enum SwapStatus {
    POSTED,              // Shift is available for claiming
    CLAIMED,             // Someone has claimed it (pending owner approval)
    OWNER_APPROVED,      // Original owner approved (pending manager)
    MANAGER_APPROVED,    // Manager approved (swap is confirmed)
    ACTIVE,              // Swap is in effect (shift is upcoming)
    COMPLETED,           // Shift has been worked
    CANCELLED,           // Swap was cancelled
    EXPIRED              // No one claimed it in time
}