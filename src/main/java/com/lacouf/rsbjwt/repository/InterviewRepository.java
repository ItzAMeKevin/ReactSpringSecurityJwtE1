package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByJobApplication_Student_Credentials_Email(String email);
}
