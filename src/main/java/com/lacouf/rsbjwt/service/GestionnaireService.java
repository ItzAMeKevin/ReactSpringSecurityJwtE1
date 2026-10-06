package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.CvStatus;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.StudentCvRepository;
import com.lacouf.rsbjwt.security.exception.InvalidCvException;
import com.lacouf.rsbjwt.service.dto.PendingCvDto;
import com.lacouf.rsbjwt.service.dto.UploadCvDto;
import com.lacouf.rsbjwt.service.mapper.GestionnaireMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final StudentCvRepository studentCvRepository;
    private final GestionnaireMapper gestionnaireMapper;
    private final CvNotificationService cvNotificationService;

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
    public void declineCv(Long cvId, UploadCvDto reviewRequest) {
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