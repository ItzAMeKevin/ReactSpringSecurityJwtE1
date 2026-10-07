package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerNotificationRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private EmployerRepository employerRepository;

    @Mock
    private JobOfferMapper jobOfferMapper;

    @Mock
    private ManagerRepository managerRepository;

    @Mock
    private ManagerNotificationRepository managerNotificationRepository;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;
    private JobOffer jobOffer;
    private JobOfferCreateDTO jobOfferCreateDTO;
    private JobOfferDetailDTO jobOfferDetailDTO;
    private AdresseDTO adresse;

    @BeforeEach
    void setUp() {
        employer = new Employer();
        employer.setId(42L);
        employer.setCompanyName("ACME Inc.");

        jobOffer = new JobOffer();
        jobOffer.setId(1L);
        jobOffer.setTitle("Développeur Java");

        adresse = AdresseDTO.builder()
                .pays("Canada").ville("Montréal").rue("Ste-Catherine")
                .numeroCivic("100").codePostal("H2X 1Y5")
                .build();
    }

    //               getJobOffres

    @Test
    @DisplayName("getJobOffres retourne la liste mappée")
    void getJobOffres_returnsMappedList() {
        // Arrange
        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(employer.getId()))
                .thenReturn(List.of(jobOffer, jobOffer));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(jobOfferDetailDTO);

        // Act
        List<JobOfferDetailDTO> result = employerService.getJobOffres(email);

        // Assert
        assertEquals(2, result.size());
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferRepository).getJobOffersByEmployerId(employer.getId());
        verify(jobOfferMapper, times(2)).toDto(jobOffer);
    }

    @Test
    @DisplayName("getJobOffres liste vide si aucune offre")
    void getJobOffres_returnsEmptyList() {
        // Arrange
        String email = "vide@test.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferRepository.getJobOffersByEmployerId(employer.getId())).thenReturn(List.of());

        // Act
        List<JobOfferDetailDTO> result = employerService.getJobOffres(email);

        // Assert
        assertTrue(result.isEmpty());
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferRepository).getJobOffersByEmployerId(employer.getId());
        verifyNoInteractions(jobOfferMapper);
    }

    //              addJobOffer

    @Test
    @DisplayName("addJobOffer retourne le DTO créé et lie l'employeur")
    void addJobOffer_returnsCreatedDto() {
        // Arrange
        JobOfferCreateDTO dto = new JobOfferCreateDTO(
                "Titre", "Desc", "Preq",
                adresse,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE
        );
        JobOfferDetailDTO expected = new JobOfferDetailDTO(
                1L, "Titre", "Desc", "Preq",
                adresse,
                "60000$", OfferStatus.WAITING,
                LocalDate.now(), LocalDate.of(2025, 3, 1),
                12, Programe.TECHNIQUES_INFORMATIQUE, "ACME Inc."
        );

        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(dto)).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(expected);

        // Act
        JobOfferDetailDTO result = employerService.addJobOffer(dto, email);

        // Assert
        assertEquals(expected, result);
        verify(employerRepository).findByCredentialsEmail(email);
        verify(jobOfferMapper).toEntity(dto);
        assertEquals(employer, jobOffer.getEmployer());
        verify(jobOfferRepository).save(jobOffer);
        verify(jobOfferMapper).toDto(jobOffer);
    }

    @Test
    @DisplayName("addJobOffer lance EmployerNotFoundException si employeur inexistant")
    void addJobOffer_throwsWhenEmployerNotFound() {
        // Arrange
        JobOfferCreateDTO dto = new JobOfferCreateDTO(
                "Titre", "Desc", "Preq",
                adresse,
                "60000$",
                LocalDate.of(2025, 3, 1),
                12,
                Programe.TECHNIQUES_INFORMATIQUE
        );
        String email = "inconnu@test.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EmployerNotFoundException.class,
                () -> employerService.addJobOffer(dto, email));

        verify(jobOfferRepository, never()).save(any());
        verify(jobOfferMapper, never()).toEntity(any());
    }
}