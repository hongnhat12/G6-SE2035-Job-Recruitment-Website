package com.se2035.jrw.entity;

import com.se2035.jrw.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Job")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JobID")
    private Integer jobId;

    // Service layer must verify recruiter.company == this company before saving.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CompanyID", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RecruiterID", nullable = false)
    private Recruiter recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IndustryID", nullable = false)
    private Industry industry;


    @Column(name = "Title", nullable = false, length = 200)
    private String title;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "Requirement", columnDefinition = "NVARCHAR(MAX)")
    private String requirement;

    @Column(name = "Benefit", columnDefinition = "NVARCHAR(MAX)")
    private String benefit;

    @Column(name = "Location", length = 200)
    private String location;

    // Validate salaryMin <= salaryMax in service layer.
    @Column(name = "SalaryMin", precision = 18, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "SalaryMax", precision = 18, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "EmploymentType", length = 50)
    private String employmentType;

    // Must be >= 0; validate in service layer.
    @Column(name = "ExperienceRequired")
    private Integer experienceRequired;

    // Replaces JobSkill junction + Skill table.
    // Store as comma-separated values, e.g. "Java,Docker,PostgreSQL"
    @Column(name = "RequiredSkills", length = 500)
    private String requiredSkills;

    @Column(name = "Deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    // Must only be set by a User with Role == ADMIN; enforce in service layer.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovedBy")
    private User approvedBy;

    @Column(name = "ApprovedAt")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    // @UpdateTimestamp replaces the missing DB trigger from the original schema.
    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SavedJob> savedJobs = new ArrayList<>();
}
