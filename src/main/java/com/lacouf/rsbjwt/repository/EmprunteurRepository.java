package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprunteurRepository extends JpaRepository<Employer, Long> {
}