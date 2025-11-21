package com.swabhiman.shiftswap.domain.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;

@Repository
public interface SwapRepository extends JpaRepository<Swap, Long> {

       List<Swap> findByOriginalOwner(Staff staff);

       List<Swap> findByClaimer(Staff staff);

       List<Swap> findByStatus(SwapStatus status);

       @Query("SELECT sw FROM Swap sw WHERE sw.status = :status " +
                     "AND sw.shift.facility.id = :facilityId")
       List<Swap> findByStatusAndFacility(SwapStatus status, Long facilityId);

       // This is the main query for the "Available Swaps" page
       @Query("SELECT sw FROM Swap sw WHERE sw.status = 'POSTED' " +
                     "AND sw.expiresAt > :now " +
                     "AND sw.shift.roleRequired = :role " +
                     "AND sw.shift.facility.id = :facilityId " +
                     "AND sw.originalOwner.id != :staffId")
       List<Swap> findAvailableSwapsForStaff(Long staffId, com.swabhiman.shiftswap.domain.enums.Role role,
                     Long facilityId, Instant now);

       // This query is for a scheduled job to clean up old swaps
       @Query("SELECT sw FROM Swap sw WHERE sw.status = 'POSTED' " +
                     "AND sw.expiresAt <= :now")
       List<Swap> findExpiredSwaps(Instant now);

       // This query populates the manager's approval queue
       @Query("SELECT sw FROM Swap sw WHERE sw.status = 'OWNER_APPROVED' " +
                     "AND sw.shift.facility.id = :facilityId " +
                     "ORDER BY sw.ownerApprovedAt ASC")
       List<Swap> findPendingManagerApproval(Long facilityId);

       // Find swap by shift (for upcoming shifts display)
       @Query("SELECT sw FROM Swap sw WHERE sw.shift.id = :shiftId " +
                     "AND sw.status IN ('POSTED', 'CLAIMED', 'OWNER_APPROVED', 'MANAGER_APPROVED')")
       List<Swap> findByShiftId(Long shiftId);
}