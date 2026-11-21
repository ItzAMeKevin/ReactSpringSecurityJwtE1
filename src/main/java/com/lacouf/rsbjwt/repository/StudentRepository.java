package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByMatricule(String matricule);
    Optional<Student> findByCredentialsEmail(String email);

    List<Student> findByPrograme(Programe programe);
}