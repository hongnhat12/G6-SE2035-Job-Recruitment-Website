package com.se2035.jrw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Candidate")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CandidateID")
    private Integer candidateId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserID", nullable = false, unique = true)
    private User user;

    @Column(name = "FullName", nullable = false, length = 100)
    private String fullName;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "Birthday")
    private LocalDate birthday;

    @Column(name = "Gender", length = 10)
    private String gender;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "Headline", length = 255)
    private String headline;

    @Column(name = "Summary", columnDefinition = "NVARCHAR(MAX)")
    private String summary;

    @Column(name = "ProfileImage", length = 255)
    private String profileImage;

    // Replaces CandidateSkill + Skill tables.
    // Store as comma-separated values, e.g. "Java,Spring Boot,SQL"
    // Extract into a proper Skill table later if search-by-skill is needed.
    @Column(name = "Skills", length = 500)
    private String skills;

    // CV files attached to this candidate
    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CV> cvList = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Application> applications = new ArrayList<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SavedJob> savedJobs = new ArrayList<>();
}
