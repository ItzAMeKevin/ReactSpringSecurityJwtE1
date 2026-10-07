package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.EmployerNotification;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.OfferStatus;
import com.lacouf.rsbjwt.repository.EmployerNotificationRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerNotificationRepository employerNotificationRepository;
    private final JobOfferMapper jobOfferMapper;

    public List<JobOfferDetailDTO> getPendingOffers() {
        return jobOfferRepository.findAllByStatus(OfferStatus.WAITING)
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    public Optional<JobOfferDetailDTO> acceptOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.ACCEPTED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre acceptée",
                    "Votre offre \"" + offer.getTitle() + "\" a été acceptée.",
                    offer.getEmployer()
            ));
            return jobOfferMapper.toDto(offer);
        });
    }

    public Optional<JobOfferDetailDTO> refuseOffer(Long id) {
        return jobOfferRepository.findById(id).map(offer -> {
            offer.setStatus(OfferStatus.REFUSED);
            jobOfferRepository.save(offer);
            employerNotificationRepository.save(new EmployerNotification(
                    "Offre refusée",
                    "Votre offre \"" + offer.getTitle() + "\" a été refusée.",
                    offer.getEmployer()
            ));
            return jobOfferMapper.toDto(offer);
        });
    }
}
