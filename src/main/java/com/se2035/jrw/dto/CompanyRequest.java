package com.se2035.jrw.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompanyRequest {
    private Integer companyId;

    @NotBlank(message = "Company name cannot be blank")
    @Size(max = 200, message = "Company name cannot exceed 200 characters")
    private String companyName;

    private String description;

    @Size(max = 255, message = "Website URL cannot exceed 255 characters")
    private String website;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    private String logo;
    private String status;
}
