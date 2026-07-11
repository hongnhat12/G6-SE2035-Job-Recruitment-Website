package com.se2035.jrw.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IndustryRequest {
    private Integer industryId;
    private String industryName;
    private String description;
}
