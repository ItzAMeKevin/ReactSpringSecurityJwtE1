package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.NotificationStatus;
import com.lacouf.rsbjwt.model.NotificationType;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.ManagerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class CvNotificationService {

    private final NotificationRepository notificationRepository;
    private final ManagerRepository managerRepository;
    private final StudentCvRepository studentCvRepository;

    @Transactional
    public void notifyManagerOnCvUpload(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
            .orElseThrow(() -> new IllegalStateException("CV not found with id: " + cvId));
        Student student = cv.getStudent();
        Manager targetManager = determineTargetManager(student);

        String title = "Nouveau CV a valider";
        String message = String.format(
            "L'etudiant %s %s (Matricule: %s) a televerse un nouveau CV: %s le %s. "
                + "Veuillez consulter et approuver le document via la plateforme.",
                student.getFirstName(),
                student.getLastName(),
                student.getMatricule(),
                cv.getFileName(),
                cv.getUploadedAt()
            );
            
            Notification notification = new Notification();
            notification.setType(NotificationType.CV_UPLOADED);
            notification.setStatus(NotificationStatus.SENT);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setManager(targetManager);
            notification.setStudentCv(cv);
            notification.setRetryCount(0);
            notification.setSentAt(Instant.now());
            
            notificationRepository.save(notification);
            log.info("Notification created for CV upload: student={}, manager={}, cv={}", 
                student.getId(), targetManager.getId(), cv.getId());
    }

    private Manager determineTargetManager(Student student) {
        if (student.getAssignedManager() != null) {
            return student.getAssignedManager();
        }
        return managerRepository.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new ManagerNotFoundException("No manager available to receive notifications"));
    }

    @Transactional
    public void notifyStudentOnCvAccepted(Student student, StudentCv cv) {
        try {
            String title = "Votre CV a ete accepte";
            String message = String.format(
                "Votre CV \"%s\" a ete valide par un gestionnaire.",
                cv.getFileName()
            );
            
            Notification notification = new Notification();
            notification.setType(NotificationType.CV_ACCEPTED);
            notification.setStatus(NotificationStatus.SENT);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setStudent(student);
            notification.setStudentCv(cv);
            notification.setSentAt(Instant.now());
            
            notificationRepository.save(notification);
            log.info("Notification sent to student on CV acceptance: student={}, cv={}", 
                student.getId(), cv.getId());
                
        } catch (Exception e) {
            log.error("Error creating acceptance notification", e);
        }
    }

    @Transactional
    public void notifyStudentOnCvDeclined(Student student, StudentCv cv) {
        try {
            String title = "Votre CV a ete refuse";
            String message = String.format(
                "Votre CV \"%s\" a ete refuse. Veuillez soumettre un nouveau CV.",
                cv.getFileName()
            );
            
            Notification notification = new Notification();
            notification.setType(NotificationType.CV_REJECTED);
            notification.setStatus(NotificationStatus.SENT);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setStudent(student);
            notification.setStudentCv(cv);
            notification.setSentAt(Instant.now());
            
            notificationRepository.save(notification);
            log.info("Notification sent to student on CV decline: student={}, cv={}", 
                student.getId(), cv.getId());
                
        } catch (Exception e) {
            log.error("Error creating decline notification", e);
        }
    }
}
