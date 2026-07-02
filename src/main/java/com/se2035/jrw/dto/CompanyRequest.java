package com.se2035.jrw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyRequest {
    private Integer companyId;
    private String companyName;
    private String description;
    private String website;
    private String email;
    private String phone;
    private String address;
    private String logo;
    private String status;
}
