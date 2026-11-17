package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CandidateMapper {

    public CandidateDto toDto(JobApplication application) {
        return toDto(
                application.getStudentCv(),
                application.getId(),
                application.getAppliedAt(),
                application.getStatus()
        );
    }

    public CandidateDto toDto(StudentCv cv, Long candidatureId, Instant appliedAt, Status status) {
        Student student = cv.getStudent();
        return new CandidateDto(
                cv.getId(),
                candidatureId,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                appliedAt,
                status
        );
    }
}
