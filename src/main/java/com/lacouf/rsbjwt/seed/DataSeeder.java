package com.lacouf.rsbjwt.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
class DataSeeder implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final CvSeeder cvSeeder;
    private final JobOfferSeeder jobOfferSeeder;
    private final JobApplicationSeeder jobApplicationSeeder;

    @Override
    public void run(String... args) {
        userSeeder.seed();
        cvSeeder.seed();
        jobOfferSeeder.seed();
        jobApplicationSeeder.seed();
    }
}
