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

import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.mapper.CandidatureMapper;
import com.lacouf.rsbjwt.service.dto.CandidateDto;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JobOfferRepository jobOfferRepository;
    private final StudentRepository studentRepository;
    private final JobOfferMapper jobOfferMapper;
    private final JobApplicationRepository jobApplicationRepository;
    private final CandidatureMapper candidatureMapper;

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

        return jobOfferRepository.findAllByStatusInAndPrograme(List.of(Status.ACCEPTED, Status.FILLED), student.getPrograme())
                .stream()
                .map(offer -> jobOfferMapper.toDto(offer)
                        .withApplication(
                                applicationStatuses.containsKey(offer.getId()),
                                applicationStatuses.get(offer.getId())))
                .toList();
    }

    public List<CandidatureDto> getHires(String studentEmail) {
        return jobApplicationRepository.findByStudentEmailAndStatus(studentEmail, Status.INTERVIEW_COMPLETED)
                .stream()
                .map(candidatureMapper::toCandidateDto)
                .toList();
    }

         public CandidatureDto acceptHire(Long id, CandidateDto candiatureDto, String name) {
                JobApplication application = jobApplicationRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException());
                if (!application.getStudentCv().getStudent().getCredentials().getEmail().equals(name)) {
                        throw new UserNotFoundException();
                }
                if (application.getStatus() != Status.INTERVIEW_COMPLETED) {
                        throw new InvalidStateTransitionException("errors.candidateNotInterviewCompleted");
                }
                application.setStatus(Status.AWAITING_CONTRACT);
                JobApplication saved = jobApplicationRepository.save(application);
                return candidatureMapper.toCandidateDto(saved);
        }

        public CandidatureDto declineHire(Long id, CandidateDto candiatureDto, String name) {
                JobApplication application = jobApplicationRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException());
                if (!application.getStudentCv().getStudent().getCredentials().getEmail().equals(name)) {
                        throw new UserNotFoundException();
                }
                if (application.getStatus() != Status.INTERVIEW_COMPLETED) {
                        throw new InvalidStateTransitionException("errors.candidateNotInterviewCompleted");
                }
                application.setStatus(Status.DECLINED);
                JobApplication saved = jobApplicationRepository.save(application);
                return candidatureMapper.toCandidateDto(saved);
        }
    
}