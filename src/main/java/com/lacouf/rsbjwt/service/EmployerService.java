package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.EmployerNotFoundException;
import com.lacouf.rsbjwt.mapper.JobOfferMapper;
import com.lacouf.rsbjwt.model.Employer;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Manager;
import com.lacouf.rsbjwt.model.ManagerNotification;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.JobOfferRepository;
import com.lacouf.rsbjwt.repository.ManagerNotificationRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.service.dto.JobOfferCreateDTO;
import com.lacouf.rsbjwt.service.dto.JobOfferDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final JobOfferRepository jobOfferRepository;
    private final EmployerRepository employerRepository;
    private final JobOfferMapper jobOfferMapper;
    private final ManagerRepository managerRepository;
    private final ManagerNotificationRepository managerNotificationRepository;

    public List<JobOfferDetailDTO> getJobOffres(String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        return jobOfferRepository.getJobOffersByEmployerId(employer.getId())
                .stream()
                .map(jobOfferMapper::toDto)
                .toList();
    }

    @Transactional
    public JobOfferDetailDTO addJobOffer(JobOfferCreateDTO jobOfferDTO, String employerEmail) {
        Employer employer = findEmployerByEmail(employerEmail);
        JobOffer jobOfferEntity = jobOfferMapper.toEntity(jobOfferDTO);
        jobOfferEntity.setEmployer(employer);
        JobOffer savedOffer = jobOfferRepository.save(jobOfferEntity);
        notifyManagers(savedOffer);
        return jobOfferMapper.toDto(savedOffer);
    }

    private void notifyManagers(JobOffer offer) {
        try {
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
            managers.forEach(manager -> sendEmailStub(manager, offer));
        } catch (Exception e) {
            System.err.println("[NOTIFICATION-ERROR] Failed to notify managers: " + e.getMessage());
        }
    }

    private void sendEmailStub(Manager manager, JobOffer offer) {
        System.out.println("[EMAIL-STUB] To: " + manager.getCredentials().getEmail()
                + " | Offre: " + offer.getTitle()
                + " | Entreprise: " + offer.getEmployer().getCompanyName());
    }

    public Employer findEmployerByEmail(String email) {
        return employerRepository.findByCredentialsEmail(email)
                .orElseThrow(() -> new EmployerNotFoundException(email));
    }
}
