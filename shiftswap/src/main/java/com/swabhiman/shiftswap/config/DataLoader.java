package com.swabhiman.shiftswap.config;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.swabhiman.shiftswap.domain.enums.CertificationType;
import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.enums.Unit;
import com.swabhiman.shiftswap.domain.enums.UserRole;
import com.swabhiman.shiftswap.domain.model.Certification; // Make sure this import is present
import com.swabhiman.shiftswap.domain.model.Facility;
import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.User;
import com.swabhiman.shiftswap.domain.repository.FacilityRepository;
import com.swabhiman.shiftswap.domain.repository.ShiftRepository;
import com.swabhiman.shiftswap.domain.repository.StaffRepository;
import com.swabhiman.shiftswap.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already populated. Skipping data load.");
            return;
        }

        log.info("Loading test data...");

        // 1. Create facility
        Facility hospital = Facility.builder()
            .name("St. Mary's General Hospital")
            .address("123 Healthcare Drive")
            .city("Springfield")
            .state("IL")
            .zipCode("62701")
            .defaultMaxWeeklyHours(60)
            .minRestHoursBetweenShifts(8)
            .build();
        facilityRepository.save(hospital);

        // 2. Create manager
        User managerUser = User.builder()
            .email("manager@hospital.com")
            .password(passwordEncoder.encode("password123"))
            .firstName("Sarah")
            .lastName("Johnson")
            .userRole(UserRole.MANAGER)
            .facility(hospital) // Fix: Assign facility to manager
            .build();
        userRepository.save(managerUser);

        // --- BOB'S USER CREATION ---
        User nurse1User = createUser("alice@hospital.com", "Alice", "Smith", UserRole.STAFF);
        User nurse2User = createUser("bob@hospital.com", "Bob", "Williams", UserRole.STAFF); // <-- Bob's User
        User cnaUser = createUser("carol@hospital.com", "Carol", "Davis", UserRole.STAFF);

        // --- BOB'S STAFF PROFILE CREATION ---
        Staff alice = createStaff(nurse1User, hospital, Role.RN, Unit.ICU);
        Staff bob = createStaff(nurse2User, hospital, Role.RN, Unit.ICU); // <-- Bob's Staff profile
        Staff carol = createStaff(cnaUser, hospital, Role.CNA, Unit.MED_SURG);

        // 5. Add certifications
        addCertification(alice, CertificationType.BLS, LocalDate.now().plusYears(2));
        addCertification(alice, CertificationType.ACLS, LocalDate.now().plusYears(1));
        addCertification(bob, CertificationType.BLS, LocalDate.now().plusYears(2)); // <-- Bob's certification
        addCertification(carol, CertificationType.BLS, LocalDate.now().plusYears(1));

        // 6. Create sample shifts
        createShift(alice, hospital, LocalDate.now().plusDays(7),
            LocalTime.of(7, 0), LocalTime.of(19, 0), Unit.ICU, Role.RN);
        createShift(alice, hospital, LocalDate.now().plusDays(14),
            LocalTime.of(7, 0), LocalTime.of(19, 0), Unit.ICU, Role.RN);
        createShift(bob, hospital, LocalDate.now().plusDays(7), // <-- Bob's shift
            LocalTime.of(19, 0), LocalTime.of(7, 0), Unit.ICU, Role.RN);
        createShift(carol, hospital, LocalDate.now().plusDays(8),
            LocalTime.of(7, 0), LocalTime.of(15, 0), Unit.MED_SURG, Role.CNA);

        log.info("Test data loaded successfully!");
        log.info("Manager login: manager@hospital.com / password123");
        log.info("Staff logins: alice@hospital.com / password123");
        log.info("              bob@hospital.com / password123"); // <-- Bob's login info
        log.info("              carol@hospital.com / password123");
    }

    private User createUser(String email, String firstName, String lastName, UserRole role) {
        User user = User.builder()
            .email(email)
            .password(passwordEncoder.encode("password123")) // <-- Correct password encoded here
            .firstName(firstName)
            .lastName(lastName)
            .userRole(role)
            .enabled(true) // Explicitly set enabled to true
            .build();
        return userRepository.save(user);
    }

    private Staff createStaff(User user, Facility facility, Role role, Unit unit) {
        Staff staff = Staff.builder()
            .user(user)
            .facility(facility)
            .role(role)
            .primaryUnit(unit)
            .maxWeeklyHours(60)
            .reliabilityScore(95.0)
            .build();
        return staffRepository.save(staff);
    }

    private void addCertification(Staff staff, CertificationType type, LocalDate expiry) {
        Certification cert = Certification.builder()
            .staff(staff)
            .type(type)
            .issuedDate(LocalDate.now().minusYears(1))
            .expiryDate(expiry)
            .certificateNumber("CERT-" + System.currentTimeMillis())
            .build();
        staff.getCertifications().add(cert);
        staffRepository.save(staff);
    }

    private void createShift(Staff staff, Facility facility, LocalDate date,
                             LocalTime start, LocalTime end, Unit unit, Role role) {
        Shift shift = Shift.builder()
            .facility(facility)
            .assignedStaff(staff)
            .date(date)
            .startTime(start)
            .endTime(end)
            .unit(unit)
            .roleRequired(role)
            .shiftType(ShiftType.REGULAR)
            .basePayRate(35.0)
            .shiftDifferential(start.getHour() >= 19 || start.getHour() < 7 ? 5.0 : 0.0)
            .build();
        shiftRepository.save(shift);
    }
}