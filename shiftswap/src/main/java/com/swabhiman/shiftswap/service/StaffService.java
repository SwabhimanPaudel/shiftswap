package com.swabhiman.shiftswap.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.User; // Use this for now
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;

    /**
     * Finds the Staff profile associated with a logged-in User.
     */
    public Staff getStaffByUser(User user) {
        return staffRepository.findByUserId(user.getId())
                // Use a standard exception for now
                .orElseThrow(() -> new EntityNotFoundException("Staff profile not found for user ID: " + user.getId()));
                // We'll replace this with SwapException later
                // .orElseThrow(() -> new SwapException("Staff profile not found for user"));
    }

    /**
     * Gets all shifts for a staff member from today onwards.
     */
    public List<Shift> getUpcomingShifts(Staff staff) {
        return shiftRepository.findUpcomingShifts(staff, LocalDate.now());
    }
}