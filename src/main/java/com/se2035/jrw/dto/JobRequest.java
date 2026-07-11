package com.se2035.jrw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class  JobRequest {
    private Integer companyId;
    private Integer recruiterId;
    private Integer industryId;
    private String title;
    private String description;
    private String requirement;
    private String benefit;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String employmentType;
    private Integer experienceRequired;
    private String requiredSkills;
    private LocalDate deadline;
}
