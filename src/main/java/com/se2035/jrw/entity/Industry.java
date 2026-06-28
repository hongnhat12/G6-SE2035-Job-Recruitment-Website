package com.se2035.jrw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Industry")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IndustryID")
    private Integer industryId;

    @Column(name = "IndustryName", nullable = false, unique = true, length = 100)
    private String industryName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();
}
