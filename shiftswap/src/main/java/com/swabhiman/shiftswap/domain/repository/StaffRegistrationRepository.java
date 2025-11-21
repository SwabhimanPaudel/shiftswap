package com.swabhiman.shiftswap.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.enums.RegistrationStatus;
import com.swabhiman.shiftswap.domain.model.StaffRegistration;

@Repository
public interface StaffRegistrationRepository extends JpaRepository<StaffRegistration, Long> {

    List<StaffRegistration> findByStatus(RegistrationStatus status);

    List<StaffRegistration> findByStatusOrderByCreatedAtDesc(RegistrationStatus status);

    Optional<StaffRegistration> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeId(String employeeId);
}
