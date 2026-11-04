package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.service.SystemeService;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemeControllerTest {

    @Mock private SystemeService service;
    @Mock private Authentication authentication;
    private SystemeController controller;

    @BeforeEach
    void setUp() {
        controller = new SystemeController(service);
    }

    @Test
    void getsNotificationsAndHandlesErrors() {
        when(service.getNotifications(authentication)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getNotifications(authentication).getStatusCode());
        when(service.getNotifications(authentication)).thenThrow(new RuntimeException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.getNotifications(authentication).getStatusCode());
    }

    @Test
    void updatesNotificationAndHandlesErrors() throws Exception {
        NotificationDto dto = NotificationDto.builder().id(1L).build();
        when(service.updateNotificationStatus(authentication, 1L)).thenReturn(dto);
        assertEquals(dto, controller.updateNotification(authentication, 1L).getBody());
        when(service.updateNotificationStatus(authentication, 2L)).thenThrow(new NotificationNotFound());
        assertEquals(HttpStatus.NOT_FOUND, controller.updateNotification(authentication, 2L).getStatusCode());
        when(service.updateNotificationStatus(authentication, 3L)).thenThrow(new RuntimeException());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, controller.updateNotification(authentication, 3L).getStatusCode());
    }
}
