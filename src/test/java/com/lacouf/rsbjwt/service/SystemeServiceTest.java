package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.NotificationNotFound;
import com.lacouf.rsbjwt.mapper.NotificationMapper;
import com.lacouf.rsbjwt.model.Notification;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.repository.NotificationRepository;
import com.lacouf.rsbjwt.service.dto.NotificationDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemeServiceTest {

    @Mock private NotificationRepository repository;
    @Mock private NotificationMapper mapper;
    @Mock private Authentication authentication;
    @InjectMocks private SystemeService service;

    @Test
    void getsNotificationsOrEmptyList() {
        when(authentication.getName()).thenReturn("user@test.com");
        NotificationDto dto = NotificationDto.builder().id(1L).build();
        when(repository.findByUser_Credentials_Email("user@test.com")).thenReturn(List.of(new Notification()));
        when(mapper.toDtoList(anyList())).thenReturn(List.of(dto));
        assertEquals(List.of(dto), service.getNotifications(authentication));

        when(repository.findByUser_Credentials_Email("user@test.com")).thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());
        assertTrue(service.getNotifications(authentication).isEmpty());
    }

    @Test
    void updatesNotificationOrThrows() throws Exception {
        when(authentication.getName()).thenReturn("user@test.com");
        Notification notification = new Notification();
        NotificationDto dto = NotificationDto.builder().id(1L).build();
        when(repository.findByIdAndUser_Credentials_Email(1L, "user@test.com")).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);
        when(mapper.toDto(notification)).thenReturn(dto);

        assertEquals(dto, service.updateNotificationStatus(authentication, 1L));
        assertEquals(Status.READ, notification.getStatus());

        when(repository.findByIdAndUser_Credentials_Email(2L, "user@test.com")).thenReturn(Optional.empty());
        assertThrows(NotificationNotFound.class,
                () -> service.updateNotificationStatus(authentication, 2L));
    }
}
