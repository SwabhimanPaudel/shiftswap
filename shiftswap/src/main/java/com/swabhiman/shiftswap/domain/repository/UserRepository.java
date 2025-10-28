package com.swabhiman.shiftswap.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swabhiman.shiftswap.domain.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA will automatically create this query for you
    // based on the method name "findByEmail"
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}