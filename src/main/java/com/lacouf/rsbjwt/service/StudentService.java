package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.security.exception.NoValidatedCvException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentCvRepository studentCvRepository;
    private final StudentRepository studentRepository;
    private final JobOfferMapper jobOfferMapper;

    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) throws NoValidatedCvException, UserNotFoundException {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(() -> new UserNotFoundException());

        if (!studentCvRepository.existsByStudentAndStatus(student, Status.ACCEPTED)) {
            throw new NoValidatedCvException();
        }

        List<JobOfferDetailDTO> acceptedJobOffers = jobOfferMapper
        .toDtoList(jobOfferRepository.findAcceptedJobOffersByStudentId(student.getId()));

        return acceptedJobOffers;
    }
}