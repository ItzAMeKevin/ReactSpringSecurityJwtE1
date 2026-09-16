package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    Optional<Manager> findFirstByFirstNameAndLastName(String firstName, String lastName);
}
