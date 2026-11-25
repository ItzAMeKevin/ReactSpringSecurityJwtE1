package com.lacouf.rsbjwt.seed;

import com.lacouf.rsbjwt.model.Programe;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.EmployerCreateDto;
import com.lacouf.rsbjwt.service.dto.ManagerCreateDto;
import com.lacouf.rsbjwt.service.dto.StudentCreateDto;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class UserSeeder {

    static final String MANAGER_EMAIL = "marie.leclerc@cegep-demo.ca";
    static final String SOPHIE_EMAIL = "sophie.tremblay@etudiant.ca";
    static final String LUCAS_EMAIL = "lucas.gagnon@etudiant.ca";
    static final String MIKA_EMAIL = "mika.amara@etudiant.ca";
    static final String MAXIME_EMAIL = "maxime.pelletier@etudiant.ca";
    static final String JAIME_EMAIL = "jaime.laguerre@etudiant.ca";
    static final String ALICE_EMAIL = "alice.bouchard@etudiant.ca";
    static final String BRUNO_EMAIL = "bruno.lacroix@etudiant.ca";
    static final String CLARA_EMAIL = "clara.morin@etudiant.ca";
    static final String CMRC_EMAIL = "jaime.paolaguere@cmr.ca";
    static final String NEXUS_EMAIL = "jp.bouchard@nexustech.ca";
    static final String HOTEL_EMAIL = "nadia.bergeron@hotellumiere.ca";

    private final UserAppService userAppService;
    private final PasswordEncoder passwordEncoder;

    void seed() {
        seedManager(MANAGER_EMAIL, "Marie", "Leclerc", "PROF-4521", "Marie1234");

        seedStudent(SOPHIE_EMAIL, "Sophie", "Tremblay", "2441532", Programe.TECHNIQUES_INFORMATIQUE, "Sophie1234");
        seedStudent(LUCAS_EMAIL, "Lucas", "Gagnon", "2441587", Programe.TECHNIQUES_INFORMATIQUE, "Lucas1234");
        seedStudent(MIKA_EMAIL, "Mika", "Amara", "2441603", Programe.TECHNIQUES_GESTION_HOTELIERE, "Mika1234");
        seedStudent(MAXIME_EMAIL, "Maxime", "Pelletier", "2441658", Programe.TECHNIQUES_LOGISTIQUE_TRANSPORT, "Maxime1234");
        seedStudent(JAIME_EMAIL, "Jaime", "Laguerre", "2441716", Programe.TECHNIQUE_GENIE_CIVIL, "Jaime1234");
        seedStudent(ALICE_EMAIL, "Alice", "Bouchard", "2441219", Programe.TECHNIQUE_GENIE_CIVIL, "Alice1234");
        seedStudent(BRUNO_EMAIL, "Bruno", "Lacroix", "2441274", Programe.TECHNIQUE_GENIE_CIVIL, "Bruno1234");
        seedStudent(CLARA_EMAIL, "Clara", "Morin", "2441345", Programe.TECHNIQUE_GENIE_CIVIL, "Clara1234");

        seedEmployer(CMRC_EMAIL, "Jaime", "Paolaguere", "Collège militaire royal du Canada",
                "613-541-6000", "CMRC-001", "Paola1234",
                new AdresseDTO("Canada", "Kingston", "General Crerar Crescent", "13", "K7K 7B4"));
        seedEmployer(NEXUS_EMAIL, "Jean-Philippe", "Bouchard", "Nexus Technologies inc.",
                "514-555-9234", "NXT-7821", "JeanPhilippe1234",
                new AdresseDTO("Canada", "Montréal", "Boul. Saint-Laurent", "4120", "H2W 1Y7"));
        seedEmployer(HOTEL_EMAIL, "Nadia", "Bergeron", "Hôtel Lumière du Fleuve",
                "514-555-0477", "HLF-3150", "Nadia1234",
                new AdresseDTO("Canada", "Montréal", "Rue de la Commune Ouest", "150", "H2Y 2C3"));
    }

    private void seedManager(String email, String firstName, String lastName, String matricule, String password) {
        createIfAbsent(ManagerCreateDto.builder()
                .email(email).firstName(firstName).lastname(lastName)
                .matricule(matricule)
                .role(Role.MANAGER).password(password)
                .build());
    }

    private void seedStudent(String email, String firstName, String lastName, String matricule,
                             Programe programe, String password) {
        createIfAbsent(StudentCreateDto.builder()
                .email(email).firstName(firstName).lastname(lastName)
                .matricule(matricule).programe(programe)
                .role(Role.STUDENT).password(password)
                .build());
    }

    private void seedEmployer(String email, String firstName, String lastName, String companyName,
                              String phoneNumber, String employerWorkId, String password, AdresseDTO adresse) {
        createIfAbsent(EmployerCreateDto.builder()
                .email(email).firstName(firstName).lastname(lastName)
                .companyName(companyName).phoneNumber(phoneNumber).employerWorkId(employerWorkId)
                .adresseDTO(adresse)
                .role(Role.EMPLOYER).password(password)
                .build());
    }

    private void createIfAbsent(UserCreateDTO user) {
        if (userAppService.getUserByEmail(user.getEmail()) != null) return;
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userAppService.inscription(user);
    }
}
