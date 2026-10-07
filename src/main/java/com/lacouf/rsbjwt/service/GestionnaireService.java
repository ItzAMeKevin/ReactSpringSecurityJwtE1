package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerNotificationRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.ManagerNotificationDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvUploadDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerNotificationRepository employerNotificationRepository;
    private final ManagerRepository managerRepository;
    private final ManagerNotificationRepository managerNotificationRepository;
    private final StudentCvRepository studentCvRepository;
    private final GestionnaireMapper gestionnaireMapper;
    private final CvNotificationService cvNotificationService;

    // ── CV Management ──────────────────────────────────────────────────────────

    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)
                .stream()
                .map(gestionnaireMapper::toPendingCvDto)
                .toList();
    }

    public byte[] getCvContent(Long cvId) {
        return studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("CV not found"))
                .getContent();
    }

    @Transactional
    public void acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("CV not found"));
        cv.setStatus(Status.ACCEPTED);
        studentCvRepository.save(cv);
        Student student = cv.getStudent();
        cvNotificationService.notifyStudentOnCvAccepted(student, cv);
    }

    @Transactional
    public void declineCv(Long cvId, CvUploadDto reviewRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("CV not found"));
        byte[] reviewContent = Base64.getDecoder().decode(reviewRequest.getContent());
        cv.setStatus(Status.DECLINED);
        cv.setReviewContent(reviewContent);
        cv.setReviewFileName(reviewRequest.getFileName());
        cv.setReviewContentType(reviewRequest.getContentType());
        studentCvRepository.save(cv);
        Student student = cv.getStudent();
        cvNotificationService.notifyStudentOnCvDeclined(student, cv);
    }

    // ── Job Offer Management ───────────────────────────────────────────────────

    public List<JobOfferDto> getPendingOffers() {
        return jobOfferRepository.findAllByStatus(Status.WAITING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public Optional<JobOfferDto> acceptOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(Status.ACCEPTED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre acceptée",
                    "Votre offre \"" + offer.getTitle() + "\" a été acceptée.",
                    offer.getEmployer()
            ));
            return toDto(offer);
        });
    }

    @Transactional
    public Optional<JobOfferDto> refuseOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(Status.DECLINED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre refusée",
                    "Votre offre \"" + offer.getTitle() + "\" a été refusée.",
                    offer.getEmployer()
            ));
            return toDto(offer);
        });
    }

    // ── Manager Notifications ──────────────────────────────────────────────────

    public List<ManagerNotificationDto> getNotifications(String managerEmail) {
        return managerRepository.findByCredentialsEmail(managerEmail)
                .map(manager -> managerNotificationRepository
                        .findAllByManager_IdOrderByCreatedAtDesc(manager.getId())
                        .stream()
                        .map(n -> new ManagerNotificationDto(
                                n.getId(), n.getTitle(), n.getMessage(),
                                n.isRead(), n.getCreatedAt(), n.getOfferId()))
                        .toList())
                .orElse(List.of());
    }

    public Optional<ManagerNotificationDto> markNotificationAsRead(String managerEmail, Long notifId) {
        return managerRepository.findByCredentialsEmail(managerEmail).flatMap(manager ->
                managerNotificationRepository.findByIdAndManager_Id(notifId, manager.getId())
                        .map(notif -> {
                            notif.markAsRead();
                            managerNotificationRepository.save(notif);
                            return new ManagerNotificationDto(
                                    notif.getId(), notif.getTitle(), notif.getMessage(),
                                    notif.isRead(), notif.getCreatedAt(), notif.getOfferId());
                        }));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private JobOfferDto toDto(JobOffer offer) {
        AdresseDTO adresseDTO = new AdresseDTO(
                offer.getAdresse().getPay(),
                offer.getAdresse().getVille(),
                offer.getAdresse().getRue(),
                offer.getAdresse().getNumeroCivic(),
                offer.getAdresse().getCodePostal()
        );
        return new JobOfferDto(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getPrerequisites(),
                adresseDTO,
                offer.getSalary(),
                offer.getStartingDate(),
                offer.getDurationInWeeks(),
                offer.getEmployer().getCompanyName(),
                offer.getPublicationDate()
        );
    }
}
