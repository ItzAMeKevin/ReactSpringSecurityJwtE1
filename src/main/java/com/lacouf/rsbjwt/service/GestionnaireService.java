package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
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
    private final JobOfferMapper jobOfferMapper;
    private final StudentCvRepository studentCvRepository;
    private final GestionnaireMapper gestionnaireMapper;
    private final CvNotificationService cvNotificationService;

    public List<JobOfferDetailDTO> getPendingOffers() {
        return jobOfferRepository.findAllByStatus(OfferStatus.WAITING)
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    public Optional<JobOfferDetailDTO> acceptOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.ACCEPTED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre acceptée",
                    "Votre offre \"" + offer.getTitle() + "\" a été acceptée.",
                    offer.getEmployer()
            ));
            return jobOfferMapper.toDto(offer);
        });
    }

    public Optional<JobOfferDetailDTO> refuseOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.REFUSED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre refusée",
                    "Votre offre \"" + offer.getTitle() + "\" a été refusée.",
                    offer.getEmployer()
            ));
            return jobOfferMapper.toDto(offer);
        });
    }

    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(CvStatus.PENDING)
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
        cv.setStatus(CvStatus.ACCEPTED);
        studentCvRepository.save(cv);
        
        Student student = cv.getStudent();
        cvNotificationService.notifyStudentOnCvAccepted(student, cv);
    }

    @Transactional
    public void declineCv(Long cvId, CvUploadDto reviewRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new InvalidCvException("CV not found"));
        byte[] reviewContent = Base64.getDecoder().decode(reviewRequest.getContent());
        cv.setStatus(CvStatus.DECLINED);
        cv.setReviewContent(reviewContent);
        cv.setReviewFileName(reviewRequest.getFileName());
        cv.setReviewContentType(reviewRequest.getContentType());
        studentCvRepository.save(cv);
        
        Student student = cv.getStudent();
        cvNotificationService.notifyStudentOnCvDeclined(student, cv);
    }
}