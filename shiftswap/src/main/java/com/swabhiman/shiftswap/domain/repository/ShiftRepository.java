package com.swabhiman.shiftswap.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.model.Shift;
import com.swabhiman.shiftswap.domain.model.Staff;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

       List<Shift> findByAssignedStaff(Staff staff);

       List<Shift> findByAssignedStaffAndDateBetween(Staff staff, LocalDate start, LocalDate end);

       @Query("SELECT s FROM Shift s WHERE s.assignedStaff = :staff " +
                     "AND s.date BETWEEN :weekStart AND :weekEnd")
       List<Shift> findByStaffAndWeek(Staff staff, LocalDate weekStart, LocalDate weekEnd);

       @Query("SELECT s FROM Shift s WHERE s.assignedStaff = :staff " +
                     "AND s.date >= :fromDate ORDER BY s.date, s.startTime")
       List<Shift> findUpcomingShifts(Staff staff, LocalDate fromDate);

       // This query checks for any overlapping shifts for a given staff member
       @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Shift s " +
                     "WHERE s.assignedStaff = :staff AND s.date = :date " +
                     "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
       boolean hasConflict(Staff staff, LocalDate date,
                     java.time.LocalTime startTime, java.time.LocalTime endTime);

       boolean existsByAssignedStaffAndDate(Staff staff, LocalDate date);
}