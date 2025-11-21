package com.swabhiman.shiftswap.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swabhiman.shiftswap.service.ShiftGenerationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ShiftGenerationController {

    private final ShiftGenerationService shiftGenerationService;

    @PostMapping("/generate-shifts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<String> generateShifts() {
        shiftGenerationService.generateForNextDays(30);
        return ResponseEntity.ok("Shifts generated successfully for the next 30 days.");
    }
}
