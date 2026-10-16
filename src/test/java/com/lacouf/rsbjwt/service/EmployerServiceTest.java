package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.*;
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
import org.mockito.ArgumentCaptor;
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
    private JobOfferCreateDTO validCreateDto() {
        return new JobOfferCreateDTO(
                "Titre", "Desc", "Preq", adresse, "60000$",
                LocalDate.of(2025, 3, 1), 12, Programe.TECHNIQUES_INFORMATIQUE);
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
                "60000$", Status.WAITING,
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

    @Test
    @DisplayName("addJobOffer crée une notification par gestionnaire")
    void addJobOffer_notifiesAllManagers() {
        // Arrange
        String email = "marie@acme.com";
        jobOffer.setPrograme(Programe.TECHNIQUES_INFORMATIQUE);
        jobOffer.setEmployer(employer);

        Manager m1 = mock(Manager.class, RETURNS_DEEP_STUBS);
        Manager m2 = mock(Manager.class, RETURNS_DEEP_STUBS);

        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any())).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);
        when(managerRepository.findAll()).thenReturn(List.of(m1, m2));

        // Act
        employerService.addJobOffer(validCreateDto(), email);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ManagerNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(managerNotificationRepository).saveAll(captor.capture());

        List<ManagerNotification> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals(m1, saved.get(0).getManager());
        assertEquals(m2, saved.get(1).getManager());

        ManagerNotification n = saved.get(0);
        assertEquals("Nouvelle offre de stage", n.getTitle());
        assertEquals(jobOffer.getId(), n.getOfferId());
        assertEquals("ACME Inc.", n.getCompanyName());
        assertEquals("Développeur Java", n.getOfferTitle());
        assertEquals("TECHNIQUES_INFORMATIQUE", n.getProgram());
        assertEquals(
                "L'entreprise \"ACME Inc.\" a déposé une offre : \"Développeur Java\" (TECHNIQUES_INFORMATIQUE)",
                n.getMessage());
        assertFalse(n.isRead());
    }

    @Test
    @DisplayName("addJobOffer sauvegarde une liste vide s'il n'y a aucun gestionnaire")
    void addJobOffer_noManagers_savesEmptyList() {
        // Arrange
        String email = "marie@acme.com";
        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any())).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);
        when(managerRepository.findAll()).thenReturn(List.of());

        // Act
        employerService.addJobOffer(validCreateDto(), email);

        // Assert
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ManagerNotification>> captor = ArgumentCaptor.forClass(List.class);
        verify(managerNotificationRepository).saveAll(captor.capture());
        assertTrue(captor.getValue().isEmpty());
    }

    @Test
    @DisplayName("addJobOffer réussit même si la notification échoue")
    void addJobOffer_notificationFailure_doesNotBreakCreation() {
        // Arrange
        String email = "marie@acme.com";
        JobOfferDetailDTO expected = mock(JobOfferDetailDTO.class);

        when(employerRepository.findByCredentialsEmail(email)).thenReturn(Optional.of(employer));
        when(jobOfferMapper.toEntity(any())).thenReturn(jobOffer);
        when(jobOfferRepository.save(jobOffer)).thenReturn(jobOffer);
        when(managerRepository.findAll()).thenThrow(new RuntimeException("DB down"));
        when(jobOfferMapper.toDto(jobOffer)).thenReturn(expected);

        // Act
        JobOfferDetailDTO result = employerService.addJobOffer(validCreateDto(), email);

        // Assert
        assertSame(expected, result);
        verify(jobOfferRepository).save(jobOffer);
        verifyNoInteractions(managerNotificationRepository);
    }

    @Test
    @DisplayName("addJobOffer ne notifie personne si l'employeur est introuvable")
    void addJobOffer_employerNotFound_noNotification() {
        // Arrange
        when(employerRepository.findByCredentialsEmail("x@test.com")).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(EmployerNotFoundException.class,
                () -> employerService.addJobOffer(validCreateDto(), "x@test.com"));
        verifyNoInteractions(managerRepository, managerNotificationRepository);
    }
}