package com.swabhiman.shiftswap.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.model.Facility;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    
    Optional<Facility> findByName(String name);
}