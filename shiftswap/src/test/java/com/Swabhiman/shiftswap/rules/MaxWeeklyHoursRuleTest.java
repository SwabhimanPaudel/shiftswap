package com.swabhiman.shiftswap.rules; // <-- This line is corrected

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.enums.Unit;
import com.swabhiman.shiftswap.domain.model.Facility;
import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.Swap;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;

@ExtendWith(MockitoExtension.class)
class MaxWeeklyHoursRuleTest {

    @Mock
    private ShiftRepository shiftRepository;

    @InjectMocks
    private MaxWeeklyHoursRule rule;

    private Staff claimer;
    private Facility facility;
    private User user;
    private Shift existingShift;
    private Shift newShift;
    private Swap swap;

    @BeforeEach
    void setUp() {
        facility = Facility.builder()
                // .id(1L) <-- This was an error. ID is auto-generated and not needed here.
                .name("Test Hospital")
                .build();

        user = User.builder()
                // .id(1L) <-- This was an error.
                .email("claimer@test.com")
                .firstName("Test")
                .lastName("User")
                .build();

        claimer = Staff.builder()
                // .id(1L) <-- This was an error.
                .user(user)
                .facility(facility)
                .role(Role.RN)
                .primaryUnit(Unit.ICU)
                .maxWeeklyHours(40)
                .build();

        LocalDate weekStart = LocalDate.now().minusDays(3);
        existingShift = Shift.builder()
                // .id(1L) <-- This was an error.
                .facility(facility)
                .assignedStaff(claimer)
                .date(weekStart)
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0)) // 8 hours
                .unit(Unit.ICU)
                .roleRequired(Role.RN)
                .shiftType(ShiftType.REGULAR) // <-- Corrected from DAY
                .basePayRate(30.0)
                .build();

        newShift = Shift.builder()
                // .id(2L) <-- This was an error.
                .facility(facility)
                .assignedStaff(claimer) // This should be a different staff, but for the rule test it's ok
                .date(weekStart.plusDays(1))
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(20, 0)) // 12 hours
                .unit(Unit.ICU)
                .roleRequired(Role.RN)
                .shiftType(ShiftType.REGULAR) // <-- Corrected from DAY
                .basePayRate(30.0)
                .build();

        swap = Swap.builder()
                // .id(1L) <-- This was an error.
                .shift(newShift)
                .originalOwner(claimer) // Not realistic, but fine for this test
                .build();
    }

    @Test
    void testValidate_WithinLimit_ReturnsValid() {
        // Existing shift is 8 hours, new shift is 12 hours = 20 total (within 40 limit)
        List<Shift> weekShifts = Arrays.asList(existingShift);
        when(shiftRepository.findByStaffAndWeek(eq(claimer), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(weekShifts);

        ValidationResult result = rule.validate(swap, claimer);

        assertTrue(result.isValid());
        assertEquals("", result.getMessage());
    }

    @Test
    void testValidate_ExceedsLimit_ReturnsInvalid() {
        // Let's make it clearly exceed: existing = 32 hours, new = 12 hours = 44 > 40
        existingShift.setEndTime(LocalTime.of(16, 0)); // 8 hours
        List<Shift> weekShifts = Arrays.asList(
                existingShift, // 8 hours
                Shift.builder()
                        // .id(3L) <-- Error
                        .facility(facility)
                        .assignedStaff(claimer)
                        .date(existingShift.getDate().plusDays(1))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(20, 0)) // 12 hours
                        .unit(Unit.ICU)
                        .roleRequired(Role.RN)
                        .shiftType(ShiftType.REGULAR) // <-- Corrected
                        .basePayRate(30.0)
                        .build(),
                Shift.builder()
                        // .id(4L) <-- Error
                        .facility(facility)
                        .assignedStaff(claimer)
                        .date(existingShift.getDate().plusDays(2))
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(20, 0)) // 12 hours
                        .unit(Unit.ICU)
                        .roleRequired(Role.RN)
                        .shiftType(ShiftType.REGULAR) // <-- Corrected
                        .basePayRate(30.0)
                        .build()
        ); // Total existing: 8 + 12 + 12 = 32. new adds 12 = 44. 44 > 40.

        when(shiftRepository.findByStaffAndWeek(eq(claimer), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(weekShifts);

        ValidationResult result = rule.validate(swap, claimer);

        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("exceeds max weekly hours"));
    }

    @Test
    void testValidate_ExactlyAtLimit_ReturnsValid() {
        // Set max to 20, existing = 8, new = 12, total = 20 (exactly at limit)
        claimer.setMaxWeeklyHours(20);
        List<Shift> weekShifts = Arrays.asList(existingShift);
        when(shiftRepository.findByStaffAndWeek(eq(claimer), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(weekShifts);

        ValidationResult result = rule.validate(swap, claimer);

        assertTrue(result.isValid());
    }
}