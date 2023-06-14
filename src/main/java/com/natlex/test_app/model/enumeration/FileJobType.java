package com.natlex.test_app.model.enumeration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "File Job Type")
public enum FileJobType {
    @JsonProperty("IMPORT")IMPORT, @JsonProperty("EXPORT")EXPORT
}
