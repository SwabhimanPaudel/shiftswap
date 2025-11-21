package com.swabhiman.shiftswap.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.Unit;
import com.swabhiman.shiftswap.domain.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByUserId(Long userId);

    List<Staff> findByFacilityId(Long facilityId);

    List<Staff> findByRole(Role role);

    List<Staff> findByPrimaryUnit(Unit unit);

    // This is a custom query using JPQL (Java Persistence Query Language)
    @Query("SELECT s FROM Staff s WHERE s.role = :role AND s.facility.id = :facilityId")
    List<Staff> findByRoleAndFacility(Role role, Long facilityId);

    Optional<Staff> findByUserEmail(String email);
}