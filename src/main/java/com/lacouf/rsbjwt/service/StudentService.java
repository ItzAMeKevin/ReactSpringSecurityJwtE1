package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentCvRepository  studentCvRepository;
    private final StudentRepository  studentRepository;
    private final JobOfferMapper jobOfferMapper;

    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        boolean hasValidatedCv = studentCvRepository
                .existsByStudentCredentialsEmailAndStatus(studentEmail, Status.ACCEPTED);
        if (!hasValidatedCv) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no validated CV");
        }
        return jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, student.getPrograme())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }
}
