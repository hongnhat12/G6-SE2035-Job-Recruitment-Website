package com.se2035.jrw.entity;

import com.se2035.jrw.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Application", uniqueConstraints = {
    @UniqueConstraint(name = "UQ_Application_Job_Candidate",
        columnNames = {"JobID", "CandidateID"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ApplicationID")
    private Integer applicationId;

    // Service layer must verify Job.Status == APPROVED before allowing apply.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JobID", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CandidateID", nullable = false)
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CVID", nullable = false)
    private CV cv;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "AppliedAt", updatable = false)
    private LocalDateTime appliedAt;

    @Column(name = "Note", columnDefinition = "NVARCHAR(MAX)")
    private String note;
}
