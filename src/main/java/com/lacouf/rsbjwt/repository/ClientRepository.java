package com.lacouf.rsbjwt.repository;

import com.lacouf.rsbjwt.model.Student;

import lombok.Data;

public interface ClientRepository {
    Student saveClient(String nom);
}

