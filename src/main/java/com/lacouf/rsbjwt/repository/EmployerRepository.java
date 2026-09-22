package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByEmployerId(String employerId);
}