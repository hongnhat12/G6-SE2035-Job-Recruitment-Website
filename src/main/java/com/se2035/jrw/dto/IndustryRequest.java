package com.se2035.jrw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndustryRequest {
    private Integer industryId;

    @NotBlank(message = "Industry name cannot be blank")
    @Size(max = 100, message = "Industry name cannot exceed 100 characters")
    private String industryName;

    private String description;
}
