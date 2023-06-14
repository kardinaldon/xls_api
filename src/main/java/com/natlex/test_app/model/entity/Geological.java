package com.natlex.test_app.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;

@Schema(description = "Geological Model")
@Data
@Entity
@Table(name="geological")
public class Geological {

    @JsonProperty("geological_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "geological_id")
    private long geologicalId;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "Geological name", example = "Geo Class 11")
    @JsonProperty("name")
    @Column(name = "name")
    private String name;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "Geological code", example = "GC11")
    @JsonProperty("code")
    @Column(name = "code")
    private String code;

}
