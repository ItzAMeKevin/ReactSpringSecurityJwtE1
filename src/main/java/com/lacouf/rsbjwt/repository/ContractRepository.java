package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ContractRepository extends JpaRepository<Contract, Long> {
    
}
