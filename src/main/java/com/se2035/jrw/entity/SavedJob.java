package com.se2035.jrw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "SavedJob")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SavedJob {

    @EmbeddedId
    private SavedJobId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "CandidateID")
    private Candidate candidate;

    // Service layer should only allow saving jobs with Status == APPROVED.
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jobId")
    @JoinColumn(name = "JobID")
    private Job job;

    @CreationTimestamp
    @Column(name = "SavedAt", updatable = false)
    private LocalDateTime savedAt;
}
