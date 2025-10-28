package com.swabhiman.shiftswap.domain.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facility extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;

    private String city;

    private String state;

    private String zipCode;

    @Column(nullable = false)
    private int defaultMaxWeeklyHours = 60;

    @Column(nullable = false)
    private int minRestHoursBetweenShifts = 8;
}