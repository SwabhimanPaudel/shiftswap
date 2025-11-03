package com.swabhiman.shiftswap.integration; // <-- This line is corrected

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.enums.SwapStatus;
import com.swabhiman.shiftswap.domain.enums.Unit;
import com.swabhiman.shiftswap.domain.enums.UserRole;
import com.swabhiman.shiftswap.domain.model.Facility;
import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.FacilityRepository;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.SwapRepository;
import com.swabhiman.shiftswap.domain.repository.UserRepository;
import com.swabhiman.shiftswap.service.SwapService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SwapWorkflowTest {

    @Autowired
    private SwapService swapService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private SwapRepository swapRepository;

    @Test
    void testCompleteSwapWorkflow() {
        // Setup: Create facility, users, and staff
        Facility facility = Facility.builder()
                .name("Test Hospital")
                .build();
        facility = facilityRepository.save(facility);

        User ownerUser = User.builder()
                .email("owner@test.com")
                .password("password")
                .firstName("Alice")
                .lastName("Owner")
                .userRole(UserRole.STAFF)
                .enabled(true)
                .build();
        ownerUser = userRepository.save(ownerUser);

        User claimerUser = User.builder()
                .email("claimer@test.com")
                .password("password")
                .firstName("Bob")
                .lastName("Claimer")
                .userRole(UserRole.STAFF)
                .enabled(true)
                .build();
        claimerUser = userRepository.save(claimerUser);

        User managerUser = User.builder()
                .email("manager@test.com")
                .password("password")
                .firstName("Charlie")
                .lastName("Manager")
                .userRole(UserRole.MANAGER)
                .enabled(true)
                .build();
        managerUser = userRepository.save(managerUser);

        Staff owner = Staff.builder()
                .user(ownerUser)
                .facility(facility)
                .role(Role.RN)
                .primaryUnit(Unit.ICU)
                .maxWeeklyHours(40)
                .build();
        owner = staffRepository.save(owner);

        Staff claimer = Staff.builder()
                .user(claimerUser)
                .facility(facility)
                .role(Role.RN)
                .primaryUnit(Unit.ICU)
                .maxWeeklyHours(40)
                .build();
        claimer = staffRepository.save(claimer);

        // Create a shift assigned to owner
        Shift shift = Shift.builder()
                .facility(facility)
                .assignedStaff(owner)
                .date(LocalDate.now().plusDays(7))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .unit(Unit.ICU)
                .roleRequired(Role.RN)
                .shiftType(ShiftType.REGULAR) // <-- This line is corrected
                .basePayRate(30.0)
                .build();
        shift = shiftRepository.save(shift);

        // Step 1: Post a swap
        Swap postedSwap = swapService.postSwap(shift.getId(), ownerUser, "Family emergency");
        assertNotNull(postedSwap);
        assertEquals(SwapStatus.POSTED, postedSwap.getStatus());
        assertEquals(owner.getId(), postedSwap.getOriginalOwner().getId());

        // Verify swap is in database
        Swap savedSwap = swapRepository.findById(postedSwap.getId()).orElse(null);
        assertNotNull(savedSwap);
        assertEquals(SwapStatus.POSTED, savedSwap.getStatus());

        // Step 2: Claim the swap
        swapService.claimSwap(postedSwap.getId(), claimerUser);

        // Verify status changed to CLAIMED
        savedSwap = swapRepository.findById(postedSwap.getId()).orElse(null);
        assertNotNull(savedSwap);
        assertEquals(SwapStatus.CLAIMED, savedSwap.getStatus());
        assertEquals(claimer.getId(), savedSwap.getClaimer().getId());
        assertNotNull(savedSwap.getClaimedAt());

        // Step 3: Owner approves
        swapService.approveByOwner(postedSwap.getId(), ownerUser);

        // Verify status changed to OWNER_APPROVED
        savedSwap = swapRepository.findById(postedSwap.getId()).orElse(null);
        assertNotNull(savedSwap);
        assertEquals(SwapStatus.OWNER_APPROVED, savedSwap.getStatus());
        assertNotNull(savedSwap.getOwnerApprovedAt());

        // Step 4: Manager approves
        swapService.approveByManager(postedSwap.getId(), managerUser);

        // Verify status changed to MANAGER_APPROVED
        savedSwap = swapRepository.findById(postedSwap.getId()).orElse(null);
        assertNotNull(savedSwap);
        assertEquals(SwapStatus.MANAGER_APPROVED, savedSwap.getStatus());
        assertEquals(managerUser.getId(), savedSwap.getApprovingManager().getId());
        assertNotNull(savedSwap.getManagerApprovedAt());

        // Verify shift has NOT been reassigned yet (that happens when status becomes ACTIVE)
        Shift updatedShift = shiftRepository.findById(shift.getId()).orElse(null);
        assertNotNull(updatedShift);
        // Shift should still be assigned to original owner until ACTIVE
        assertEquals(owner.getId(), updatedShift.getAssignedStaff().getId());
    }
}