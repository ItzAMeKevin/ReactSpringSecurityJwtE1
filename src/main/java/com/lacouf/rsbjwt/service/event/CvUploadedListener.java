package com.lacouf.rsbjwt.service.event;

import com.lacouf.rsbjwt.service.CvNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class CvUploadedListener {

    private final CvNotificationService cvNotificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCvUploaded(CvUploadedEvent event) {
        try {
            cvNotificationService.notifyManagerOnCvUpload(event.cvId());
        } catch (Exception e) {
            log.error("Erreur lors de la notification du CV {}", event.cvId(), e);
        }
    }
}