package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.SystemManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<SystemManager, Long> {

    Optional<SystemManager> findFirstByFirstNameAndLastName(String firstName, String lastName);
}
