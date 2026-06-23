package com.se2035.jrw.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "CV")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CVID")
    private Integer cvId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CandidateID", nullable = false)
    private Candidate candidate;

    @Column(name = "CVName", length = 200)
    private String cvName;

    @Column(name = "FilePath", length = 255)
    private String filePath;

    // Only one CV per candidate may be true.
    // Enforce in service: before setting true, reset all others to false for this candidate.
    @Column(name = "IsDefault")
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "UploadedAt", updatable = false)
    private LocalDateTime uploadedAt;
}
