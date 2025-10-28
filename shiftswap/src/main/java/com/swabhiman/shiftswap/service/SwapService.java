package com.swabhiman.shiftswap.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.SwapRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwapService {

    private final SwapRepository swapRepository;
    private final ShiftRepository shiftRepository;
    private final StaffRepository staffRepository;
    // We'll add StateMachine, AuditService, EventPublisher, and Rules later

    /**
     * Finds the Staff profile associated with a logged-in User.
     * (We added this here to avoid circular dependency with StaffService for now)
     */
    private Staff getStaffByUser(User user) {
        return staffRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Staff profile not found for user ID: " + user.getId()));
    }

    /**
     * Gets all swaps that are currently POSTED and available for the user to claim.
     */
    public List<Swap> getAvailableSwaps(User user) {
        Staff staff = getStaffByUser(user);
        // Using the complex query from your repository
        return swapRepository.findAvailableSwapsForStaff(
            staff.getId(),
            staff.getRole(),
            staff.getFacility().getId(),
            Instant.now()
        );
    }

    /**
     * Creates a new Swap record for one of the user's shifts.
     */
    @Transactional // Ensures this happens as a single database transaction
    public Swap postSwap(Long shiftId, User user, String reason) {
        Staff poster = getStaffByUser(user);
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new EntityNotFoundException("Shift not found with ID: " + shiftId));

        // Basic validation (more rules can be added later)
        if (!shift.getAssignedStaff().getId().equals(poster.getId())) {
            throw new IllegalArgumentException("You can only post swaps for your own shifts");
        }
        if (shift.getDate().isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("Cannot swap shifts in the past");
        }

        // Check if a swap already exists for this shift (basic check)
        // A more robust check might look for non-cancelled/non-completed swaps
        if (swapRepository.findByStatus(SwapStatus.POSTED).stream().anyMatch(s -> s.getShift().getId().equals(shiftId))) {
             throw new IllegalArgumentException("A swap request for this shift is already posted.");
        }


        Swap swap = Swap.builder()
                .shift(shift)
                .originalOwner(poster)
                .status(SwapStatus.POSTED)
                .reason(reason)
                .postedAt(Instant.now())
                // Simple expiration - 48 hours from now
                .expiresAt(Instant.now().plusSeconds(48 * 60 * 60))
                .build();

        log.info("Posting swap for shift ID: {} by user: {}", shiftId, user.getEmail());
        return swapRepository.save(swap);
    }

    /**
     * Claims an available swap and reassigns the shift.
     * (Simplified: No approvals needed)
     */
    @Transactional
    public void claimSwap(Long swapId, User user) {
        Staff claimer = getStaffByUser(user);
        Swap swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new EntityNotFoundException("Swap not found with ID: " + swapId));

        // Basic validation
        if (swap.getStatus() != SwapStatus.POSTED || swap.isExpired()) {
            throw new IllegalArgumentException("This swap is no longer available to claim.");
        }
        if (swap.getOriginalOwner().getId().equals(claimer.getId())) {
            throw new IllegalArgumentException("You cannot claim your own swap.");
        }

        Shift shift = swap.getShift();

        // --- Core Logic: Reassign the shift ---
        shift.setAssignedStaff(claimer);
        shiftRepository.save(shift);

        // --- Update the Swap status ---
        swap.setClaimer(claimer);
        swap.setClaimedAt(Instant.now());
        // Simplified: Go directly to COMPLETED (no approvals)
        swap.setStatus(SwapStatus.COMPLETED); // Or maybe a new "APPROVED" status
        swapRepository.save(swap);

        log.info("Swap ID: {} claimed by user: {}. Shift ID: {} reassigned.", swapId, user.getEmail(), shift.getId());
    }

     /**
     * Gets swaps posted by the current user.
     */
     public List<Swap> getMyPostedSwaps(User user) {
         Staff staff = getStaffByUser(user);
         return swapRepository.findByOriginalOwner(staff);
     }

     /**
     * Gets swaps claimed by the current user.
     */
     public List<Swap> getMyClaimedSwaps(User user) {
        Staff staff = getStaffByUser(user);
        return swapRepository.findByClaimer(staff);
     }
}