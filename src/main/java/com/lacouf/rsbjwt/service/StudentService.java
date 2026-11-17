package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentRepository studentRepository;
    private final JobOfferMapper jobOfferMapper;
    private final JobApplicationRepository jobApplicationRepository;

    public List<JobOfferDetailDTO> getAcceptedJobOffers(String studentEmail) throws UserNotFoundException {
        Student student = studentRepository.findByCredentialsEmail(studentEmail)
                .orElseThrow(() -> new UserNotFoundException());

        Map<Long, Status> applicationStatuses = jobApplicationRepository
                .findByStudentIdWithOffer(student.getId())
                .stream()
                .collect(Collectors.toMap(
                        ja -> ja.getJobOffer().getId(),
                        JobApplication::getStatus,
                        (mostRecent, older) -> mostRecent
                ));

        return jobOfferRepository.findAllByStatusAndPrograme(Status.ACCEPTED, student.getPrograme())
                .stream()
                .map(offer -> jobOfferMapper.toDto(offer)
                        .withApplication(
                                applicationStatuses.containsKey(offer.getId()),
                                applicationStatuses.get(offer.getId())))
                .toList();
    }
}