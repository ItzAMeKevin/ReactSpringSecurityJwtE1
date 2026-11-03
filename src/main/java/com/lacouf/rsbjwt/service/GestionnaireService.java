package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.JobOffer;

import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.ManagerNotificationDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvDeclineFeedbackDto;
import com.lacouf.rsbjwt.service.event.CvDecisionEvent;
import com.lacouf.rsbjwt.mapper.NotificationMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.service.dto.NotificationDto;

@Service
@RequiredArgsConstructor
public class GestionnaireService {
    
    private static final String JOB_OFFER_UPLOADED = "JOB_OFFER_UPLOADED";
    private static final Pattern OFFER_MESSAGE_PATTERN = Pattern.compile(
        "L'entreprise \"(.*?)\" a déposé une offre : \"(.*?)\" \\((.*?)\\)"
    );
    
    private final UserAppRepository userAppRepository;
    private final JobOfferRepository jobOfferRepository;

    private final ManagerRepository managerRepository;
    private final NotificationRepository notificationRepository;

    private final StudentCvRepository studentCvRepository;

    private final NotificationMapper notificationMapper;
    private final CvMapper cvMapper;

    private final JobOfferMapper jobOfferMapper;


    private final ApplicationEventPublisher eventPublisher;


    GestionnaireService(UserAppRepository userAppRepository) {
        this.userAppRepository = userAppRepository;
    }


    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)
                .stream()
                .map(cvMapper::toDto)
                .toList();
    }

    public byte[] getCvContent(Long cvId) {
        return studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"))
                .getContent();
    }

    @Transactional
    public void acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"));
        cv.setStatus(Status.ACCEPTED);
        studentCvRepository.save(cv);
        eventPublisher.publishEvent(new CvDecisionEvent(cv.getId()));
    }

    @Transactional
    public void declineCv(Long cvId, CvDeclineFeedbackDto feedbackRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"));
        cv.setStatus(Status.DECLINED);
        cv.setReviewFeedback(feedbackRequest.getFeedback());
        studentCvRepository.save(cv);
        eventPublisher.publishEvent(new CvDecisionEvent(cv.getId()));
    }


    public List<JobOfferDto> getPendingOffers() {
        return jobOfferMapper.toDtoListJobOffers(jobOfferRepository.findAllByStatus(Status.WAITING));
    }

    @Transactional
    public Optional<JobOfferDto> acceptOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(Status.ACCEPTED);
            jobOfferRepository.save(offer);
            notificationRepository.save(Notification.builder()
                    .message("notification.types.JOB_OFFER_ACCEPTED.message")
                    .user(offer.getEmployer())
                    .notificationType(NotificationType.JOB_OFFER)
                    .referenceId(id)
                    .build());
            return(jobOfferMapper.toDto(offer));
        }).orElse(JobOfferDto.builder().erreur("errors.jobOfferNotFound").build());
    }

    @Transactional
    public Optional<JobOfferDto> refuseOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(Status.DECLINED);
            jobOfferRepository.save(offer);
            notificationRepository.save(Notification.builder()
                    .message("notification.types.JOB_OFFER_DECLINED.message")
                    .user(offer.getEmployer())
                    .notificationType(NotificationType.JOB_OFFER)
                    .referenceId(id)
                    .build());
            return(jobOfferMapper.toDto(offer));
        }).orElse(JobOfferDto.builder().erreur("errors.jobOfferNotFound").build());
    }
}
