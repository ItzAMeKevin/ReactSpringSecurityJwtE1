package com.lacouf.rsbjwt.mapper;

import com.lacouf.rsbjwt.model.Interview;
import com.lacouf.rsbjwt.model.InterviewSlot;
import com.lacouf.rsbjwt.service.dto.EmployerInterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewDto;
import com.lacouf.rsbjwt.service.dto.InterviewResponseDto;
import com.lacouf.rsbjwt.service.dto.InterviewSlotDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = AdresseMapper.class)
public interface InterviewMapper {

    @Mapping(source = "jobApplication.id", target = "candidatureId")
    @Mapping(source = "adresse", target = "address")
    InterviewResponseDto toDto(Interview interview);

    @Mapping(source = "jobApplication.id", target = "candidatureId")
    @Mapping(source = "jobApplication.jobOffer.id", target = "offerId")
    @Mapping(source = "jobApplication.jobOffer.title", target = "offerTitle")
    @Mapping(source = "jobApplication.studentCv.student.firstName", target = "studentFirstName")
    @Mapping(source = "jobApplication.studentCv.student.lastName", target = "studentLastName")
    @Mapping(source = "jobApplication.studentCv.student.email", target = "studentEmail")
    @Mapping(source = "adresse", target = "address")
    EmployerInterviewDto toEmployerDto(Interview interview);

    @Mapping(source = "jobApplication.id", target = "candidatureId")
    @Mapping(source = "adresse", target = "address")
    @Mapping(source = "jobApplication.jobOffer.employer.companyName", target = "companyName")
    @Mapping(source = "jobApplication.jobOffer.title", target = "offerTitle")
    @Mapping(target = "expired", ignore = true)
    InterviewDto toInterviewDto(Interview interview);

    InterviewSlotDto toSlotDto(InterviewSlot slot);
}
