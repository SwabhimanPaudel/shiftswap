package com.swabhiman.shiftswap.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.StaffWorkPattern;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.StaffWorkPatternRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftGenerationService {

    private final StaffRepository staffRepository;
    private final StaffWorkPatternRepository staffWorkPatternRepository;
    private final ShiftRepository shiftRepository;

    /**
     * Generates shifts for the next 30 days for all staff based on their work
     * patterns.
     * Runs automatically every day at 1 AM.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void generateWeeklyShifts() {
        log.info("Starting automatic shift generation...");
        generateForNextDays(30);
        log.info("Shift generation completed.");
    }

    @Transactional
    public void generateForNextDays(int daysToGenerate) {
        List<Staff> allStaff = staffRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Staff staff : allStaff) {
            List<StaffWorkPattern> patterns = staffWorkPatternRepository.findByStaff(staff);
            if (patterns.isEmpty()) {
                continue;
            }

            for (int i = 0; i < daysToGenerate; i++) {
                LocalDate targetDate = today.plusDays(i);

                // Check if staff has a pattern for this day of week
                for (StaffWorkPattern pattern : patterns) {
                    if (pattern.getDayOfWeek() == targetDate.getDayOfWeek()) {
                        createShiftIfNotExists(staff, pattern, targetDate);
                    }
                }
            }
        }
    }

    private void createShiftIfNotExists(Staff staff, StaffWorkPattern pattern, LocalDate date) {
        // Check if shift already exists for this staff on this date
        if (shiftRepository.existsByAssignedStaffAndDate(staff, date)) {
            return; // Skip if exists
        }

        Shift newShift = Shift.builder()
                .assignedStaff(staff)
                .facility(staff.getFacility())
                .date(date)
                .startTime(pattern.getStartTime())
                .endTime(pattern.getEndTime())
                .unit(staff.getPrimaryUnit()) // Use staff's primary unit
                .roleRequired(staff.getRole()) // Use staff's role
                .shiftType(pattern.getShiftType())
                .basePayRate(25.0) // Default base pay, could be dynamic
                .shiftDifferential(0.0)
                .build();

        shiftRepository.save(newShift);
        log.debug("Generated shift for {} on {}", staff.getUser().getFullName(), date);
    }
}
