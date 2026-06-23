package com.se2035.jrw.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class SavedJobId implements Serializable {

    private Integer candidateId;
    private Integer jobId;
}
