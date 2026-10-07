package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.User;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final EmployerMapper employerMapper;
    private final ManagerMapper managerMapper;
    private final StudentMapper studentMapper;

    public UserMapper(EmployerMapper employerMapper,
                      ManagerMapper managerMapper,
                      StudentMapper studentMapper) {
        this.employerMapper = employerMapper;
        this.managerMapper = managerMapper;
        this.studentMapper = studentMapper;
    }

    public User toEntity(UserCreateDTO dto) {
        return switch (dto.getRole()) {
            case EMPLOYER -> employerMapper.toEntity(dto);
            case MANAGER -> managerMapper.toEntity(dto);
            case STUDENT -> studentMapper.toEntity(dto);
            default -> throw new IllegalArgumentException("Unknown role: " + dto.getRole());
        };
    }

    public UserCreateDTO toDto(User user) {
        return switch (user) {
            case Employer employer -> employerMapper.toDto(employer);
            case Manager manager -> managerMapper.toDto(manager);
            case Student student -> studentMapper.toDto(student);
            default -> throw new IllegalArgumentException("Unknown user type");
        };
    }
}