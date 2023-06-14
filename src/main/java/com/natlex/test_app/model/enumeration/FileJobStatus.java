package com.natlex.test_app.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "FileJob Status")
public enum FileJobStatus {
    @JsonProperty("IN PROGRESS")IN_PROGRESS, @JsonProperty("DONE")DONE, @JsonProperty("ERROR")ERROR
}
