package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.UploadCvDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final StudentCvRepository studentCvRepository;
    private final NotificationRepository notificationRepository;
    private final GestionnaireMapper gestionnaireMapper;

    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(CvStatus.PENDING)
                .stream()
                .map(gestionnaireMapper::toPendingCvDto)
                .toList();
    }

    public byte[] getCvContent(Long cvId) {
        return studentCvRepository.findById(cvId)
                .orElseThrow(UserNotFoundException::new)
                .getContent();
    }

    @Transactional
    public void acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(UserNotFoundException::new);
        cv.setStatus(CvStatus.ACCEPTED);
        studentCvRepository.save(cv);
        createNotification(cv.getStudent(),
                "Votre CV a été accepté",
                "Votre CV\"" + cv.getFileName() + "\" a été validé par un gestionnaire.");
    }

    @Transactional
    public void declineCv(Long cvId, UploadCvDto reviewRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(UserNotFoundException::new);
        byte[] reviewCOntent = Base64.getDecoder().decode(reviewRequest.getContent());
        cv.setStatus(CvStatus.DECLINED);
        cv.setReviewContent(reviewCOntent);
        cv.setReviewFileName(reviewRequest.getFileName());
        cv.setReviewContentType(reviewRequest.getContentType());
        studentCvRepository.save(cv);
        createNotification(cv.getStudent(),
                "Votre CV a été refusé",
                "Votre CV\"" + cv.getFileName() + "\" a été refusé. Veuillez soumettre un nouveau CV.");

    }

    private void createNotification(Student student, String title, String message) {
        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }
}
