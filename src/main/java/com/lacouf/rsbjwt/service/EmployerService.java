package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lacouf.rsbjwt.model.Status;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerRepository employerRepository;
    private final JobOfferMapper jobOfferMapper;
    private final ManagerRepository managerRepository;
    private final NotificationRepository notificationRepository;

    public List<JobOfferDetailDTO> getJobOffres(String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        return jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    @Transactional
    public JobOfferDetailDTO addJobOffer(JobOfferCreateDTO jobOfferDTO, String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        JobOffer jobOfferEntity = jobOfferMapper.toEntity(jobOfferDTO);
        jobOfferEntity.setEmployer(employer);
        JobOffer savedOffer = jobOfferRepository.save(jobOfferEntity);
        Notification notification = Notification.builder()
                .message("notification.types.JOB_OFFER_CREATED.message")
                .user(employer)
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(savedOffer.getId())
                .build();
        notificationRepository.save(notification);
        return jobOfferMapper.toDto(savedOffer);
    }

    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new EmployerNotFoundException(email));
    }

    @Transactional
    public Optional<JobOfferDetailDTO> updateRefusedJobOffer(Long id, JobOfferCreateDTO dto, String employerEmail) {
        return jobOfferRepository.findByIdAndEmployerCredentialsEmail(id, employerEmail).map(offer -> {
            if (offer.getStatus() != Status.DECLINED) {
                throw new IllegalStateException("errors.jobOfferUpdateFailed");
            }
            JobOffer updated = jobOfferMapper.toEntity(dto);
            offer.setTitle(updated.getTitle());
            offer.setDescription(updated.getDescription());
            offer.setPrerequisites(updated.getPrerequisites());
            offer.setSalary(updated.getSalary());
            offer.setStartingDate(updated.getStartingDate());
            offer.setDurationInWeeks(updated.getDurationInWeeks());
            offer.setPrograme(updated.getPrograme());
            offer.setAdresse(updated.getAdresse());
            offer.setStatus(Status.PENDING);
            return jobOfferMapper.toDto(jobOfferRepository.save(offer));
        });
    }
}
