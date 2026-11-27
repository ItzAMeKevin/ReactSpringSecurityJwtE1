package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.exception.*;
import com.lacouf.rsbjwt.mapper.*;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GestionnaireService {

    private final JobOfferRepository jobOfferRepository;

    private final NotificationRepository notificationRepository;

    private final StudentCvRepository studentCvRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final CvMapper cvMapper;

    private final JobOfferMapper jobOfferMapper;

    private final CandidatureMapper candidatureMapper;

    private final JobApplicationSuiviMapper jobApplicationSuiviMapper;

    private final ContractRepository contractRepository;

    private final ContractMapper contractMapper;

    private final StudentRepository studentRepository;

    private final EmployerRepository employerRepository;

    private final ManagerRepository managerRepository;

    private final InternshipSiteEvaluationRepository evaluationRepository;


    @Transactional(readOnly = true)
    public List<PendingCvDto> getPendingCvs() {
        return studentCvRepository.findAllByStatusOrderByUploadedAtAsc(Status.PENDING)
                .stream()
                .map(cvMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] getCvContent(Long cvId) {
        return studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"))
                .getContent();
    }

    @Transactional
    public PendingCvDto acceptCv(Long cvId) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"));
        cv.setStatus(Status.ACCEPTED);
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_ACCEPTED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
        return cvMapper.toDto(cv);
    }

    @Transactional
    public void declineCv(Long cvId, FeedbackDto feedbackRequest) {
        StudentCv cv = studentCvRepository.findById(cvId)
                .orElseThrow(() -> new CvException("errors.cvNotFound"));
        cv.setStatus(Status.DECLINED);
        cv.setReviewFeedback(feedbackRequest.getFeedback());
        studentCvRepository.save(cv);
        this.notificationRepository.save(Notification.builder()
                .message("notification.types.CV_DECLINED.message")
                .user(cv.getStudent())
                .notificationType(NotificationType.CV)
                .referenceId(cv.getId())
                .build());
    }


    @Transactional(readOnly = true)
    public List<JobOfferDto> getPendingOffers() {
        return jobOfferMapper.toDtoListJobOffers(jobOfferRepository.findAllByStatus(Status.PENDING));
    }


    @Transactional
    public JobOfferDto acceptOffer(Long id) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.ACCEPTED, "notification.types.JOB_OFFER_ACCEPTED.message", null);
    }

    @Transactional
    public JobOfferDto refuseOffer(Long id, FeedbackDto feedbackRequest) throws JobOfferNotFoundException, InvalidStateTransitionException {
        return changeOfferStatus(id, Status.DECLINED, "notification.types.JOB_OFFER_DECLINED.message", feedbackRequest.getFeedback());
    }

    @Transactional(readOnly = true)
    public List<JobApplicationSuiviDto> getAllCandidatures() {
        return jobApplicationSuiviMapper.toDtoList(jobApplicationRepository.findAllWithStudentAndOffer());
    }

    private JobOfferDto changeOfferStatus(Long id, Status newStatus, String messageKey, String feedback) throws JobOfferNotFoundException, InvalidStateTransitionException {
        JobOffer offer = jobOfferRepository
                .findById(id)
                .orElseThrow(() -> new JobOfferNotFoundException("errors.jobOfferNotFound"));

        if (offer.getStatus() != Status.PENDING) {
            throw new InvalidStateTransitionException("errors.jobOfferInvalidState");
        }

        offer.setStatus(newStatus);
        if (feedback != null) {
            offer.setRefusalFeedback(feedback);
        }
        jobOfferRepository.save(offer);

        notificationRepository.save(Notification.builder()
                .message(messageKey)
                .user(offer.getEmployer())
                .notificationType(NotificationType.JOB_OFFER)
                .referenceId(offer.getId())
                .build());

        return jobOfferMapper.toDtoSimple(offer);
    }

    @Transactional
    public ContractDto createContract(ContractDto contractDto) {
        Contract contract = contractMapper.toEntity(contractDto);
        contract.setStatus(Status.UNSIGNED);
        if (contract.getContent() != null) {
            contract.setSize(contract.getContent().length);
        }

        Student student = studentRepository.findById(contractDto.getStudentId())
                .orElseThrow(UserNotFoundException::new);
        Employer employer = employerRepository.findById(contractDto.getEmployerId())
                .orElseThrow(UserNotFoundException::new);
        Manager manager = managerRepository.findById(contractDto.getManagerId())
                .orElseThrow(UserNotFoundException::new);

        contract = contractRepository.save(contract);
        contract.setStudent(student);
        contract.setEmployer(employer);
        contract.setManager(manager);

        student.setContract(contract);
        employer.setContract(contract);
        manager.setContract(contract);
        studentRepository.save(student);
        employerRepository.save(employer);
        managerRepository.save(manager);

        return contractMapper.toDto(contract);
    }

    // ── Internship Site Evaluation ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<PendingInternshipSiteDto> getPendingInternshipSites() {
        return jobApplicationRepository.findByStatusWithoutEvaluation(Status.WORKING).stream()
                .map(this::toPendingInternshipSiteDto)
                .toList();
    }

    @Transactional
    public void submitInternshipSiteEvaluation(Long applicationId, InternshipSiteEvaluationDto dto) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(ApplicationNotFoundException::new);
        if (application.getStatus() != Status.WORKING) {
            throw new InvalidStateTransitionException("errors.applicationNotWorking");
        }
        if (evaluationRepository.existsByJobApplicationId(applicationId)) {
            throw new OfferAlreadyProcessedException("errors.evaluationAlreadySubmitted");
        }
        evaluationRepository.save(buildEvaluation(dto, application));
    }

    private PendingInternshipSiteDto toPendingInternshipSiteDto(JobApplication application) {
        Student student = application.getStudent();
        JobOffer offer = application.getJobOffer();
        String startDate = offer.getStartingDate() != null ? offer.getStartingDate().toString() : null;
        return new PendingInternshipSiteDto(
                application.getId(),
                student.getFirstName(),
                student.getLastName(),
                offer.getEmployer().getCompanyName(),
                startDate
        );
    }

    private InternshipSiteEvaluation buildEvaluation(InternshipSiteEvaluationDto dto, JobApplication application) {
        InternshipSiteEvaluation evaluation = new InternshipSiteEvaluation();
        evaluation.setJobApplication(application);
        evaluation.setCompanyName(dto.nomEntreprise());
        evaluation.setContactPerson(dto.personneContact());
        evaluation.setAddress(dto.adresse());
        evaluation.setPhone(dto.telephone());
        evaluation.setCity(dto.ville());
        evaluation.setFax(dto.telecopieur());
        evaluation.setPostalCode(dto.codePostal());
        evaluation.setInternName(dto.nomStagiaire());
        evaluation.setInternshipDate(dto.dateStage());
        evaluation.setInternshipNumber(parseIntSafe(dto.stageNumero()));
        evaluation.setTasks(dto.taches());
        evaluation.setOnboarding(dto.accueil());
        evaluation.setSupervision(dto.encadrement());
        evaluation.setSupervisionHoursMonth1(parseIntSafe(dto.heuresMois1()));
        evaluation.setSupervisionHoursMonth2(parseIntSafe(dto.heuresMois2()));
        evaluation.setSupervisionHoursMonth3(parseIntSafe(dto.heuresMois3()));
        evaluation.setSalary(dto.salaire());
        evaluation.setHourlyRate(dto.salaireParHeure());
        evaluation.setCommunication(dto.communication());
        evaluation.setEquipment(dto.equipement());
        evaluation.setWorkload(dto.volume());
        evaluation.setHealthSafety(dto.hygiene());
        evaluation.setWorkAtmosphere(dto.climat());
        evaluation.setPublicTransit(dto.transport());
        evaluation.setComments(dto.commentaires());
        evaluation.setPreferredInternshipNumber(parseIntSafe(dto.stagePrivilegie()));
        evaluation.setMaxInterns(dto.nbStagiaires());
        evaluation.setWelcomeSameIntern("oui".equalsIgnoreCase(dto.memeStagiaire()));
        evaluation.setHasVariableShifts("oui".equalsIgnoreCase(dto.quartsVariables()));
        evaluation.setShift1Start(dto.quart1De());
        evaluation.setShift1End(dto.quart1A());
        evaluation.setShift2Start(dto.quart2De());
        evaluation.setShift2End(dto.quart2A());
        evaluation.setShift3Start(dto.quart3De());
        evaluation.setShift3End(dto.quart3A());
        evaluation.setTeacherSignature(dto.signature());
        evaluation.setSignatureDate(dto.dateSignature());
        return evaluation;
    }

    private static Integer parseIntSafe(String value) {
        if (value == null || value.isBlank()) return null;
        try { return Integer.parseInt(value); } catch (NumberFormatException e) { return null; }
    }
}
