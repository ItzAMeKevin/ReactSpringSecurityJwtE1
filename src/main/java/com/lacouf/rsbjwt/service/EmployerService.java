package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Adresse;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.ManagerNotification;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerNotificationRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDto;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final EmployerRepository employerRepository;
    private final JobOfferRepository jobOfferRepository;
    private final ManagerRepository managerRepository;
    private final ManagerNotificationRepository managerNotificationRepository;


    @Transactional
    public Optional<JobOfferDto> submitOffer(String employerEmail, JobOfferCreateDto dto) {
        return employerRepository.findByCredentialsEmail(employerEmail)
                .map(employer -> {
                    Adresse adresse = new Adresse(
                            dto.adresse().pay(),
                            dto.adresse().ville(),
                            dto.adresse().rue(),
                            dto.adresse().numeroCivic(),
                            dto.adresse().codePostal()
                    );


                    JobOffer offer = JobOffer.builder()
                            .title(dto.title())
                            .description(dto.description())
                            .prerequisites(dto.prerequisites())
                            .adresse(adresse)
                            .salary(dto.salary())
                            .startingDate(dto.startingDate())
                            .durationInWeeks(dto.durationInWeeks())
                            .programe(dto.programe())
                            .employer(employer)
                            .build();

                    JobOffer saved = jobOfferRepository.save(offer);
                    notifyManagers(saved);
                    return toDto(saved);

                });
    }
    private void notifyManagers(JobOffer offer) {
        List<Manager> managers = managerRepository.findAll();
        List<ManagerNotification> notifs = managers.stream()
                .map(manager -> new ManagerNotification(
                        "Nouvelle offre de stage",
                        "L'entreprise \"" + offer.getEmployer().getCompanyName()
                                + "\" a déposé une offre : \"" + offer.getTitle()
                                + "\" (" + offer.getPrograme() + ")",
                        offer.getId(),
                        manager
                ))
                .toList();
        managerNotificationRepository.saveAll(notifs);
        managers.forEach(manager -> logEmail(manager, offer));
    }

    private void logEmail(Manager manager, JobOffer offer) {
        System.out.println("[EMAIL-STUB] To: " + manager.getCredentials().getEmail()
                + " | Offre: " + offer.getTitle()
                + " | Entreprise: " + offer.getEmployer().getCompanyName());
    }


    private JobOfferDto toDto(JobOffer offer) {
        AdresseDTO adresseDTO = new AdresseDTO(
                offer.getAdresse().getPay(),
                offer.getAdresse().getVille(),
                offer.getAdresse().getRue(),
                offer.getAdresse().getNumeroCivic(),
                offer.getAdresse().getCodePostal()
        );
        return new JobOfferDto(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getPrerequisites(),
                adresseDTO,
                offer.getSalary(),
                offer.getStartingDate(),
                offer.getDurationInWeeks(),
                offer.getEmployer().getCompanyName(),
                offer.getPublicationDate()
        );
    }


}
