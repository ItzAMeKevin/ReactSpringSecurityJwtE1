package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {

    @Mock private JobOfferRepository jobOfferRepository;
    @Mock private EmployerRepository employerRepository;
    @Mock private JobOfferMapper jobOfferMapper;
    @Mock private SystemeService systemeService;
    @InjectMocks private EmployerService service;

    private Employer employer() {
        Employer employer = new Employer();
        employer.setId(1L);
        employer.setCompanyName("ACME");
        return employer;
    }

    private JobOfferCreateDTO request() {
        return new JobOfferCreateDTO("Java", "Build APIs", "Spring", null, "60000",
                LocalDate.now(), 12, Programe.TECHNIQUES_INFORMATIQUE);
    }

    @Test
    void getsOffersAndMapsThem() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        JobOfferDetailDTO dto = JobOfferDetailDTO.builder().id(1L).build();
        when(employerRepository.findByCredentialsEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(1L)).thenReturn(List.of(offer));
        when(jobOfferMapper.toDto(offer)).thenReturn(dto);

        assertEquals(List.of(dto), service.getJobOffres("employer@test.com"));
    }

    @Test
    void addsOfferAndNotifiesManagers() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        offer.setId(3L);
        when(employerRepository.findByCredentialsEmail("employer@test.com")).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any(JobOfferCreateDTO.class))).thenReturn(offer);
        when(jobOfferRepository.save(offer)).thenReturn(offer);
        when(jobOfferMapper.toDto(offer)).thenReturn(JobOfferDetailDTO.builder().id(3L).build());

        assertEquals(3L, service.addJobOffer(request(), "employer@test.com").getId());
        assertSame(employer, offer.getEmployer());
        verify(systemeService).notifyManagers(NotificationType.JOB_OFFER, 3L, "notification.types.JOB_OFFER_CREATED.message");
    }

    @Test
    void rejectsUnknownEmployer() {
        when(employerRepository.findByCredentialsEmail("missing@test.com")).thenReturn(Optional.empty());
        assertThrows(EmployerNotFoundException.class,
                () -> service.getJobOffres("missing@test.com"));
    }

    @Test
    void updatesOnlyDeclinedOffer() {
        Employer employer = employer();
        JobOffer offer = new JobOffer();
        offer.setStatus(Status.DECLINED);
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(1L, "employer@test.com"))
                .thenReturn(Optional.of(offer));
        when(jobOfferMapper.toEntity(any(JobOfferCreateDTO.class))).thenReturn(new JobOffer());
        when(jobOfferRepository.save(offer)).thenReturn(offer);
        when(jobOfferMapper.toDto(offer)).thenReturn(JobOfferDetailDTO.builder().build());

        assertTrue(service.updateRefusedJobOffer(1L, request(), "employer@test.com").isPresent());
        assertEquals(Status.PENDING, offer.getStatus());

        offer.setStatus(Status.ACCEPTED);
        assertThrows(IllegalStateException.class,
                () -> service.updateRefusedJobOffer(1L, request(), "employer@test.com"));
        when(jobOfferRepository.findByIdAndEmployerCredentialsEmail(2L, "employer@test.com"))
                .thenReturn(Optional.empty());
        assertTrue(service.updateRefusedJobOffer(2L, request(), "employer@test.com").isEmpty());
    }
}
