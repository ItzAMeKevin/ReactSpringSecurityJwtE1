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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentCvRepository studentCvRepository;
    private final StudentRepository studentRepository;
    private final JobOfferMapper jobOfferMapper;

    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(UserNotFoundException::new);
        boolean hasValidatedCv = studentCvRepository
                .findFirstByStudentCredentialsEmailOrderByUploadedAtDesc(studentEmail)
                .map(cv -> cv.getStatus() == Status.ACCEPTED)
                .orElse(false);
        if (!hasValidatedCv) {
            throw new NoValidatedCvException();
        }

        Set<Long> appliedOfferIds =
                jobOfferRepository.findAppliedOfferIdsByStudentId(student.getId());

        return jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, student.getPrograme())
                .stream()
                .map(offer -> jobOfferMapper.toDto(offer)
                        .withApplied(appliedOfferIds.contains(offer.getId())))
                .toList();
    }
}