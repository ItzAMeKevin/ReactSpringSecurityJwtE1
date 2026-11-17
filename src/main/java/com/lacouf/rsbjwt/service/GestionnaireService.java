package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.InvalidStateTransitionException;
import com.lacouf.rsbjwt.exception.JobOfferNotFoundException;
import com.lacouf.rsbjwt.mapper.CvMapper;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.FeedbackDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;

    private final NotificationRepository notificationRepository;

    private final StudentCvRepository studentCvRepository;

    private final CvMapper cvMapper;

    private final JobOfferMapper jobOfferMapper;


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
    public PendingCvDto acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"));
        cv.setStatus(Status.ACCEPTED);
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_ACCEPTED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
        return cvMapper.toDto(cv);
    }

    @Transactional
    public void declineCv(Long cvId, FeedbackDto feedbackRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("errors.cvNotFound"));
        cv.setStatus(Status.DECLINED);
        cv.setReviewFeedback(feedbackRequest.getFeedback());
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_DECLINED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
    }


    public List<JobOfferDto> getPendingOffers() {
        return jobOfferMapper.toDtoListJobOffers(jobOfferRepository.findAllByStatus(Status.PENDING));
    }


    @Transactional
    public JobOfferDto acceptOffer(Long id) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.ACCEPTED, "notification.types.JOB_OFFER_ACCEPTED.message", null);
    }

    @Transactional
    public JobOfferDto refuseOffer(Long id, FeedbackDto feedbackRequest) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.DECLINED, "notification.types.JOB_OFFER_DECLINED.message", feedbackRequest.getFeedback());
    }

    private JobOfferDto changeOfferStatus(Long id, Status newStatus, String messageKey, String feedback) throws JobOfferNotFoundException, InvalidStateTransitionException {
        JobOffer offer = jobOfferRepository
                .findById(id)
                .orElseThrow(() -> new JobOfferNotFoundException("errors.jobOfferNotFound"));

        if (offer.getStatus() != Status.PENDING) {
            throw new InvalidStateTransitionException("errors.jobOfferInvalidState");
        }

        offer.setStatus(newStatus);
        if (feedback != null) {
            offer.setRefusalFeedback(feedback);
        }
        jobOfferRepository.save(offer);

        notificationRepository.save(Notification.builder()
                .message(messageKey)
                .user(offer.getEmployer())
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(offer.getId())
                .build());

        return jobOfferMapper.toDtoSimple(offer);
    }


}
