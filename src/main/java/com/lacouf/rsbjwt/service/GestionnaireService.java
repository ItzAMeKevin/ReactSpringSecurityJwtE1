package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerNotificationRepository employerNotificationRepository;

    public List<JobOfferDto> getPendingOffers() {
        return jobOfferRepository.findAllByStatus(OfferStatus.WAITING)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public Optional<JobOfferDto> acceptOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.ACCEPTED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre acceptée",
                    "Votre offre \"" + offer.getTitle() + "\" a été acceptée.",
                    offer.getEmployer()
            ));
            return toDto(offer);
        });
    }

    public Optional<JobOfferDto> refuseOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.REFUSED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre refusée",
                    "Votre offre \"" + offer.getTitle() + "\" a été refusée.",
                    offer.getEmployer()
            ));
            return toDto(offer);
        });
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
