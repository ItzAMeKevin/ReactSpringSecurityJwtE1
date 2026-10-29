package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.ManagerNotification;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import com.lacouf.rsbjwt.service.dto.ManagerNotificationDto;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.CvDeclineFeedbackDto;
import com.lacouf.rsbjwt.service.event.CvDecisionEvent;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GestionnaireService {
    private static final String JOB_OFFER_UPLOADED = "JOB_OFFER_UPLOADED";
    private static final Pattern OFFER_MESSAGE_PATTERN = Pattern.compile(
            "L'entreprise \"(.*?)\" a déposé une offre : \"(.*?)\" \\((.*?)\\)"
    );

    private final JobOfferRepository jobOfferRepository;
    private final EmployerNotificationRepository employerNotificationRepository;
    private final ManagerRepository managerRepository;
    private final ManagerNotificationRepository managerNotificationRepository;
    private final StudentCvRepository studentCvRepository;
    private final GestionnaireMapper gestionnaireMapper;
    private final ApplicationEventPublisher eventPublisher;


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
        eventPublisher.publishEvent(new CvDecisionEvent(cv.getId()));
    }

    @Transactional
    public void declineCv(Long cvId, CvDeclineFeedbackDto feedbackRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("CV not found"));
        cv.setStatus(Status.DECLINED);
        cv.setReviewFeedback(feedbackRequest.getFeedback());
        studentCvRepository.save(cv);
        eventPublisher.publishEvent(new CvDecisionEvent(cv.getId()));
    }


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


    @Transactional(readOnly = true)
    public List<ManagerNotificationDto> getNotifications(String managerEmail) {
        return managerRepository.findByCredentialsEmail(managerEmail)
                .map(manager -> managerNotificationRepository
                        .findAllByManager_IdOrderByCreatedAtDesc(manager.getId())
                        .stream()
                        .map(this::toNotificationDto)
                        .toList())
                .orElse(List.of());
    }

    @Transactional
    public Optional<ManagerNotificationDto> markNotificationAsRead(String managerEmail, Long notifId) {
        return managerRepository.findByCredentialsEmail(managerEmail).flatMap(manager ->
                managerNotificationRepository.findByIdAndManager_Id(notifId, manager.getId())
                        .map(notif -> {
                            notif.markAsRead();
                            managerNotificationRepository.save(notif);
                            return toNotificationDto(notif);
                        }));
    }

    private ManagerNotificationDto toNotificationDto(ManagerNotification notif) {
        OfferInfo info = resolveOfferInfo(notif);
        return new ManagerNotificationDto(
                notif.getId(),
                notif.getTitle(),
                notif.getMessage(),
                notif.isRead(),
                notif.getCreatedAt(),
                notif.getOfferId(),
                JOB_OFFER_UPLOADED,
                info.company(),
                info.title(),
                info.program()
        );
    }

    private OfferInfo resolveOfferInfo(ManagerNotification notif) {
        OfferInfo info = new OfferInfo(
                nullToEmpty(notif.getCompanyName()),
                nullToEmpty(notif.getOfferTitle()),
                nullToEmpty(notif.getProgram())
        );
        if (!info.isComplete()) {
            info = info.fillMissingFrom(offerInfoFromDatabase(notif.getOfferId()));
        }
        if (!info.isComplete()) {
            info = info.fillMissingFrom(offerInfoFromMessage(notif.getMessage()));
        }
        return info;
    }

    private OfferInfo offerInfoFromDatabase(Long offerId) {
        if (offerId == null) {
            return OfferInfo.EMPTY;
        }
        return jobOfferRepository.findById(offerId)
                .map(offer -> new OfferInfo(
                        offer.getEmployer() == null ? "" : nullToEmpty(offer.getEmployer().getCompanyName()),
                        nullToEmpty(offer.getTitle()),
                        offer.getPrograme() == null ? "" : offer.getPrograme().name()
                ))
                .orElse(OfferInfo.EMPTY);
    }

    private OfferInfo offerInfoFromMessage(String message) {
        Matcher matcher = OFFER_MESSAGE_PATTERN.matcher(nullToEmpty(message));
        return matcher.find()
                ? new OfferInfo(matcher.group(1), matcher.group(2), matcher.group(3))
                : OfferInfo.EMPTY;
    }

    private record OfferInfo(String company, String title, String program) {

        static final OfferInfo EMPTY = new OfferInfo("", "", "");

        boolean isComplete() {
            return !company.isEmpty() && !title.isEmpty() && !program.isEmpty();
        }

        OfferInfo fillMissingFrom(OfferInfo other) {
            return new OfferInfo(
                    company.isEmpty() ? other.company : company,
                    title.isEmpty() ? other.title : title,
                    program.isEmpty() ? other.program : program
            );
        }
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
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
