package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.SystemManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GestionnaireRepository extends JpaRepository<SystemManager, Long> {
}