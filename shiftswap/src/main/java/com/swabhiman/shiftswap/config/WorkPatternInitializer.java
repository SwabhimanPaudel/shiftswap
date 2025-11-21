package com.swabhiman.shiftswap.config;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.model.StaffWorkPattern;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.StaffWorkPatternRepository;
import com.swabhiman.shiftswap.service.ShiftGenerationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WorkPatternInitializer {

    private final StaffRepository staffRepository;
    private final StaffWorkPatternRepository staffWorkPatternRepository;
    private final ShiftGenerationService shiftGenerationService;

    @Bean
    public CommandLineRunner initWorkPatterns() {
        return args -> {
            initializePatterns();
            // Generate shifts for the next 30 days immediately on startup
            shiftGenerationService.generateForNextDays(30);
        };
    }

    @Transactional
    public void initializePatterns() {
        // Alice: Mon, Thu, Fri, Sun
        assignPattern("alice@hospital.com", Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY));

        // Bob: Tue, Wed, Sat, Sun
        assignPattern("bob@hospital.com", Arrays.asList(
                DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY));

        // Carol: Mon, Wed, Fri, Sat
        assignPattern("carol@hospital.com", Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY));

        // Ram: Tue, Thu, Fri, Sun
        assignPattern("kc.ram@gmail.com", Arrays.asList(
                DayOfWeek.TUESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SUNDAY));
    }

    private void assignPattern(String email, List<DayOfWeek> days) {
        staffRepository.findByUserEmail(email).ifPresent(staff -> {
            if (staffWorkPatternRepository.findByStaff(staff).isEmpty()) {
                log.info("Assigning work pattern to {}", staff.getUser().getFullName());
                for (DayOfWeek day : days) {
                    StaffWorkPattern pattern = StaffWorkPattern.builder()
                            .staff(staff)
                            .dayOfWeek(day)
                            .shiftType(ShiftType.REGULAR)
                            .startTime(LocalTime.of(7, 0)) // 7 AM
                            .endTime(LocalTime.of(19, 0)) // 7 PM
                            .build();
                    staffWorkPatternRepository.save(pattern);
                }
            }
        });
    }
}
