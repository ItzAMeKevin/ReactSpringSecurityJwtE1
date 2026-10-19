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
public class CvDecisionListenerTest {

    @Mock
    private CvNotificationService cvNotificationService;

    @InjectMocks
    private CvDecisionListener cvDecisionListener;

    @Test
    void onCvDecision_delegatesToNotificationService() {
        // ARRANGE
        CvDecisionEvent event = new CvDecisionEvent(42L);

        // ACT
        cvDecisionListener.onCvDecision(event);

        // ASSERT
        verify(cvNotificationService, times(1)).notifyStudentOnCvDecision(42L);
    }

    @Test
    void onCvDecision_serviceFails_doesNotPropagateException() {
        // ARRANGE
        CvDecisionEvent event = new CvDecisionEvent(42L);
        doThrow(new IllegalStateException("boom")).when(cvNotificationService).notifyStudentOnCvDecision(42L);

        // ACT & ASSERT
        assertDoesNotThrow(() -> cvDecisionListener.onCvDecision(event));
    }
}
