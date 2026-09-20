package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.StudentCv;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentCvRepository extends JpaRepository<StudentCv, Long> {
    List<StudentCv> findAllByStudent_IdOrderByUploadedAtDesc(Long studentId);

    Optional<StudentCv> findByIdAndStudent_Id(Long id, Long studentId);}
