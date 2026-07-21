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

    @Column(name = "SalaryMin", precision = 18, scale = 2)
    private BigDecimal salaryMin;

    @Column(name = "SalaryMax", precision = 18, scale = 2)
    private BigDecimal salaryMax;

    @Column(name = "EmploymentType", length = 50)
    private String employmentType;

    @Column(name = "ExperienceRequired")
    private Integer experienceRequired;

    @Column(name = "RequiredSkills", length = 500)
    private String requiredSkills;

    @Column(name = "Deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ApprovedBy")
    private User approvedBy;

    @Column(name = "ApprovedAt")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RejectedBy")
    private User rejectedBy;

    @Column(name = "RejectedAt")
    private LocalDateTime rejectedAt;

    @CreationTimestamp
    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

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
