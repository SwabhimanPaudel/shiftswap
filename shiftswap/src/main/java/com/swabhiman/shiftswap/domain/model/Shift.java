package com.swabhiman.shiftswap.domain.model;

import com.swabhiman.shiftswap.domain.enums.Role;
import com.swabhiman.shiftswap.domain.enums.ShiftType;
import com.swabhiman.shiftswap.domain.enums.Unit;
import jakarta.persistence.*;
import lombok.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "assigned_staff_id", nullable = false)
    private Staff assignedStaff;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role roleRequired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShiftType shiftType; // The enum you just created

    @Column(nullable = false)
    private double basePayRate;

    private double shiftDifferential = 0.0; // Extra pay for nights/weekends

    // --- Helper Methods ---

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.of(date, startTime);
    }

    public LocalDateTime getEndDateTime() {
        // Handle overnight shifts
        LocalDateTime end = LocalDateTime.of(date, endTime);
        if (endTime.isBefore(startTime)) {
            end = end.plusDays(1);
        }
        return end;
    }

    public Duration getDuration() {
        return Duration.between(getStartDateTime(), getEndDateTime());
    }

    public long getHours() {
        return getDuration().toHours();
    }

    public LocalDate getWeekOf() {
        // Return Monday of the week this shift falls in
        return date.minusDays(date.getDayOfWeek().getValue() - 1);
    }

    public boolean isOvernight() {
        return endTime.isBefore(startTime);
    }

    public double getTotalPay() {
        return (basePayRate + shiftDifferential) * getHours();
    }
}