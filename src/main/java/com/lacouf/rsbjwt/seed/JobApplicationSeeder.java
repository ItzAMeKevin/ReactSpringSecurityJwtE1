package com.lacouf.rsbjwt.seed;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Status;
import com.lacouf.rsbjwt.model.StudentCv;
import com.lacouf.rsbjwt.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.lacouf.rsbjwt.seed.CvSeeder.*;
import static com.lacouf.rsbjwt.seed.JobOfferSeeder.CMRC_OFFER_TITLE;
import static com.lacouf.rsbjwt.seed.UserSeeder.*;

@Component
@RequiredArgsConstructor
class JobApplicationSeeder {

    private final CvSeeder cvSeeder;
    private final JobOfferSeeder jobOfferSeeder;
    private final JobApplicationRepository jobApplicationRepository;

    @Transactional
    void seed() {
        JobOffer cmrcOffer = jobOfferSeeder.findOffer(CMRC_EMAIL, CMRC_OFFER_TITLE)
                .orElseThrow(() -> new IllegalStateException("Seed: offre introuvable " + CMRC_OFFER_TITLE));

        seedApplication(cmrcOffer, ALICE_EMAIL, ALICE_CV);
        seedApplication(cmrcOffer, BRUNO_EMAIL, BRUNO_CV);
        seedApplication(cmrcOffer, CLARA_EMAIL, CLARA_CV);
    }

    private void seedApplication(JobOffer offer, String studentEmail, String cvFileName) {
        StudentCv cv = cvSeeder.findCv(studentEmail, cvFileName)
                .orElseThrow(() -> new IllegalStateException("Seed: CV introuvable " + cvFileName));
        if (jobApplicationRepository.existsByStudentCv_IdAndJobOffer_Id(cv.getId(), offer.getId())) return;
        offer.getAppliedCvs().add(cv);
        JobApplication application = new JobApplication();
        application.setJobOffer(offer);
        application.setStudentCv(cv);
        application.setStudent(cv.getStudent());
        application.setStatus(Status.ACCEPTED);
        jobApplicationRepository.save(application);
    }
}
