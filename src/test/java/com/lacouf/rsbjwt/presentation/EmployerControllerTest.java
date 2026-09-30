package com.lacouf.rsbjwt.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lacouf.rsbjwt.model.Role;
import com.lacouf.rsbjwt.repository.EmployerRepository;
import com.lacouf.rsbjwt.repository.ManagerRepository;
import com.lacouf.rsbjwt.repository.StudentRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.AdresseDTO;
import com.lacouf.rsbjwt.service.dto.UserCreateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmployerControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private UserAppService userAppService;

    private ObjectMapper objectMapper;

    @MockitoBean
    private StudentRepository emprunteurRepository;

    @MockitoBean
    private EmployerRepository preposeRepository;

    @MockitoBean
    private ManagerRepository managerRepository;

    @MockitoBean
    private UserAppRepository userAppRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        userCreateDTO.setId(2L);
        userCreateDTO.setFirstName("Marie");
        userCreateDTO.setLastname("Tremblay");
        userCreateDTO.setEmail("marie@acme.com");
        userCreateDTO.setPassword("password123");
        userCreateDTO.setRole(Role.EMPLOYER);
        userCreateDTO.setCompanyName("ACME Inc.");
        userCreateDTO.setAddress("123 rue Principale");
        userCreateDTO.setPostalCode("H1H1H1");
        userCreateDTO.setCity("Montréal");
        userCreateDTO.setPhoneNumber("514-555-1234");
        userCreateDTO.setEmployerId("EMP001");

        AdresseDTO location = new AdresseDTO(
                "Canada",
                "Montréal",
                "Sainte-Catherine",
                "100",
                "H2X 1Y5"
        );

        
    }

    @Test
    @DisplayName("Post /employer/addJobOffer")
    void add_Job_Offer_succes() throws Exception{


    }

    @Test
    void getJobOffre() {
    }
}