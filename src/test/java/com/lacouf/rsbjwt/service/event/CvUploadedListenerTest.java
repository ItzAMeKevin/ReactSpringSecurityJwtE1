package com.lacouf.rsbjwt.service.event;

import com.lacouf.rsbjwt.service.CvNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CvUploadedListenerTest {

    @Mock
    private CvNotificationService cvNotificationService;

    @InjectMocks
    private CvUploadedListener cvUploadedListener;

    @Test
    void onCvUploaded_delegatesToNotificationService() {
        // ARRANGE
        CvUploadedEvent event = new CvUploadedEvent(42L);

        // ACT
        cvUploadedListener.onCvUploaded(event);

        // ASSERT
        verify(cvNotificationService, times(1)).notifyManagerOnCvUpload(42L);
    }

    @Test
    void onCvUploaded_serviceFails_doesNotPropagateException() {
        // ARRANGE
        CvUploadedEvent event = new CvUploadedEvent(42L);
        doThrow(new IllegalStateException("boom")).when(cvNotificationService).notifyManagerOnCvUpload(42L);

        // ACT & ASSERT
        assertDoesNotThrow(() -> cvUploadedListener.onCvUploaded(event));
    }
}