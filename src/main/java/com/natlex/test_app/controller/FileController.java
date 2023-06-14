package com.natlex.test_app.controller;

import com.natlex.test_app.model.entity.FileJob;
import com.natlex.test_app.model.enumeration.FileJobStatus;
import com.natlex.test_app.model.enumeration.FileJobType;
import com.natlex.test_app.model.exception.*;
import com.natlex.test_app.service.IFileJobService;
import com.natlex.test_app.service.IFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping(path = "#{'${values.url.file.controller}'}")
@Validated
public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private IFileService fileService;
    @Autowired
    private IFileJobService fileJobService;

    @Operation(summary = "Import"
            , description = "imports the file and returns the job id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = {@Content(schema = @Schema(implementation = Long.class)
                    , mediaType = "application/json")})
            , @ApiResponse(responseCode = "400"
            , description = "the service is intended for files with the extension .xls"
            , content = @Content)
            , @ApiResponse(responseCode = "400"
            , description = "failed to read file"
            , content = @Content)
            , @ApiResponse(responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @PostMapping(path = "#{'${values.url.file.import}'}"
            , consumes = {MULTIPART_FORM_DATA_VALUE}, produces = "application/json")
    public ResponseEntity<Long> importFile(
            @Parameter(name = "file", description = ".xls file in form_data request body", required = true)
            @RequestParam("file")
            @NonNull
            MultipartFile file) {
        if (Objects.equals(file.getContentType(),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                || Objects.equals(file.getContentType(), "application/vnd.ms-excel")
        ) {
            try {
                byte[] bytes = file.getBytes();
                FileJob fileJob = new FileJob();
                fileJob.setStatus(FileJobStatus.IN_PROGRESS);
                fileJob.setType(FileJobType.IMPORT);
                long fileJobId = fileJobService.save(fileJob).getJobId();
                fileService.store(bytes, fileJobId);
                return ResponseEntity.ok()
                        .body(fileJobId);
            } catch (IOException e) {
                LOG.error(" failed to read file " + file.getName());
                throw new FileHandlingException("failed to read file");
            }
        } else {
            LOG.warn(file.getName() + " - is not an xls file");
            throw new InvalidFileException();
        }
    }

    @Operation(summary = "Job status "
            , description = " returns the status of the job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = {@Content(schema = @Schema(implementation = FileJobStatus.class)
                    , mediaType = "application/json")})
            , @ApiResponse(responseCode = "400"
            , description = "FileJob not not found"
            , content = @Content)
            , @ApiResponse(responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(path = "#{'${values.url.file.import}'}" + "/{id}", produces = "application/json")
    public ResponseEntity<FileJobStatus> getResultOfImportingByJobID(
            @Parameter(name = "id", description = "job id in path parameter", required = true)
            @PathVariable
            @NonNull
            long id) {
        Optional<FileJob> fileJob = fileJobService.getFileJob(id);
        if (fileJob.isPresent()) {
            return ResponseEntity.ok()
                    .body(fileJob.get().getStatus());
        } else {
            LOG.warn("file job with id " + id + " not found");
            throw new FileJobNotFoundException();
        }
    }

    @Operation(summary = "Export "
            , description = " exports records from the database and returns the job id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = {@Content(schema = @Schema(implementation = Long.class)
                    , mediaType = "application/json")})
            , @ApiResponse(responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(path = "#{'${values.url.file.export}'}", produces = "application/json")
    public ResponseEntity<Long> export() {
        FileJob fileJob = new FileJob();
        fileJob.setStatus(FileJobStatus.IN_PROGRESS);
        fileJob.setType(FileJobType.EXPORT);
        fileJob = fileJobService.save(fileJob);
        fileService.exportFromDbToFile(fileJob);
        return ResponseEntity.ok()
                .body(fileJob.getJobId());
    }

    @Operation(summary = "Export Progress"
            , description = " returns the status of the export")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = {@Content(schema = @Schema(implementation = FileJobStatus.class)
                    , mediaType = "application/json")})
            , @ApiResponse(responseCode = "400"
            , description = "FileJob not not found"
            , content = @Content)
            , @ApiResponse(responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
    })
    @GetMapping(path = "#{'${values.url.file.export}'}" + "/{id}"
            , produces = "application/json")
    public ResponseEntity<FileJobStatus> exportProgress(
            @Parameter(name = "id", description = "job id in path parameter", required = true)
            @PathVariable
            @NonNull
            long id) {
        Optional<FileJob> fileJob = fileJobService.getFileJob(id);
        if (fileJob.isPresent()) {
            return ResponseEntity.ok()
                    .body(fileJob.get().getStatus());
        } else {
            LOG.warn("file job with id " + id + " not found");
            throw new FileJobNotFoundException();
        }
    }

    @Operation(summary = "Download"
            , description = " downloads the file if the work on its export is completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"
                    , description = "successful"
                    , content = {@Content(schema = @Schema(implementation = Resource.class)
                    , mediaType = "application/json")})
            , @ApiResponse(responseCode = "400"
            , description = "FileJob not not found"
            , content = @Content)
            , @ApiResponse(responseCode = "401"
            , description = "unauthorized"
            , content = @Content)
            , @ApiResponse(responseCode = "403"
            , description = "downloading the file is not possible at the moment"
            , content = @Content)

    })
    @GetMapping(path = "#{'${values.url.file.export}'}" + "/{id}/file", produces = "application/json")
    public ResponseEntity<Resource> fileByJobID(
            @Parameter(name = "id", description = "job id in path parameter", required = true)
            @PathVariable
            @NonNull
            int id) {
        Optional<FileJob> fileJob = fileJobService.getFileJob(id);
        if (fileJob.isPresent()) {
            if (!fileJob.get().getStatus().equals(FileJobStatus.DONE)) {
                throw new FileJobStatusException(fileJob.get().getStatus());
            }
            Resource resourceByJobID;
            try {
                resourceByJobID = fileService.getByJobID(fileJob.get());
            }
            catch (MalformedURLException e) {
                LOG.error("file " + fileJob.get().getFilePath()
                        + " - not found, the file path might be in the wrong format"
                + e.getMessage());
                throw new FileStorageException("file " + fileJob.get().getFilePath()
                        + " - not found, the file path might be in the wrong format");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION
                            , "attachment; filename=\""
                                    + resourceByJobID.getFilename() + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(resourceByJobID);
        } else {
            LOG.warn("file job with id " + id + " - not found");
            throw new FileJobNotFoundException();
        }
    }
}
