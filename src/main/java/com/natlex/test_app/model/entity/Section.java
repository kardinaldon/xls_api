package com.natlex.test_app.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Schema(description = "Section Model")
@Data
@Entity
@Table(name="section")
public class Section {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY
            , description = "section id", example = "1")
    @JsonProperty("section_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private long sectionId;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "Section name", example = "Section 1")
    @JsonProperty("name")
    @Column(name = "name")
    private String name;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "List of geological classes")
    @JsonProperty("geologicalClasses")
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Geological> geologicalClasses;

}

