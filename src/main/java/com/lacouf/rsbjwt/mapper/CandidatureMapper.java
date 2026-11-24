package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.JobApplication;
import com.lacouf.rsbjwt.model.JobOffer;
import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.service.dto.CandidatureDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidatureMapper {

    default CandidatureDto toCandidateDto(JobApplication application) {
        Student student = application.getStudentCv().getStudent();
        JobOffer offer = application.getJobOffer();

        return new CandidatureDto(
                application.getId(),
                new CandidatureDto.StudentInfoDto(
                        student.getFirstName(),
                        student.getLastName(),
                        student.getMatricule(),
                        student.getEmail()
                ),
                new CandidatureDto.JobOfferInfoDto(
                        offer.getTitle(),
                        offer.getEmployer().getCompanyName(),
                        offer.getDurationInWeeks()
                ),
                application.getStatus(),
                application.getAppliedAt(),
                application.getUpdatedAt()
        );
    }

    default List<CandidatureDto> toDtoList(List<JobApplication> applications) {
        return applications.stream()
                .map(this::toCandidateDto)
                .toList();
    }
}
