package com.natlex.test_app.model.entity;

import com.natlex.test_app.model.enumeration.FileJobStatus;
import com.natlex.test_app.model.enumeration.FileJobType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;

@Schema(description = "FileJob Model")
@Data
@Entity
@Table(name="file_job")
public class FileJob {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "FileJob Id", example = "123")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private long jobId;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "FileJob status", example = "DONE")
    @Column(name = "status")
    private FileJobStatus status;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "FileJob type", example = "EXPORT")
    @Column(name = "type")
    private FileJobType type;

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "path to .xls file", example = "E:\\example\\example.xls")
    @Column(name = "file_path")
    private String filePath = "";

    @Schema(accessMode = Schema.AccessMode.READ_WRITE
            , description = "error message", example = "file parsing error")
    @Column(name = "message")
    private String message = "";
}
