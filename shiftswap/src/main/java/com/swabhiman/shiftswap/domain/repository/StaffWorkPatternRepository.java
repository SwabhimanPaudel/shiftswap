package com.swabhiman.shiftswap.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.model.Staff;
import com.swabhiman.shiftswap.domain.model.StaffWorkPattern;

@Repository
public interface StaffWorkPatternRepository extends JpaRepository<StaffWorkPattern, Long> {

    List<StaffWorkPattern> findByStaff(Staff staff);

    void deleteByStaff(Staff staff);
}
