package com.se2035.jrw.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Company")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompanyID")
    private Integer companyId;

    @Column(name = "CompanyName", nullable = false, length = 200)
    private String companyName;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "Website", length = 255)
    private String website;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "Phone", length = 20)
    private String phone;

    @Column(name = "Address", length = 255)
    private String address;

    @Column(name = "Logo", length = 255)
    private String logo;

    @Column(name = "Status", length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Recruiter> recruiters = new ArrayList<>();

    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();
}
