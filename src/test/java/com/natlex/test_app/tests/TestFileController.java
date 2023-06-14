package com.natlex.test_app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natlex.test_app.controller.FileController;
import com.natlex.test_app.model.entity.FileJob;
import com.natlex.test_app.model.enumeration.FileJobStatus;
import com.natlex.test_app.model.enumeration.FileJobType;
import com.natlex.test_app.service.IFileJobService;
import com.natlex.test_app.service.IFileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.file.Path;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(value = "admin")
@WebMvcTest(FileController.class)
public class TestFileController {
    @MockBean
    private IFileService fileService;
    @MockBean
    private IFileJobService fileJobService;
    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper mapper;
    private FileJob fileJob;
    @Value("${values.input_folder}")
    private Path inputFolder;
    @Value("${values.url.file.controller}")
    private String urlFileController;
    @Value("${values.url.file.export}")
    private String urlFileExport;
    @Value("${values.url.file.import}")
    private String urlFileImport;
    @Value("${values.test_xls_file}")
    private String testXlsFile;

    @Test
    void importFile() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.IN_PROGRESS);
        fileJob.setType(FileJobType.IMPORT);
        MockMultipartFile file = new MockMultipartFile(
                "file"
                , "data/xls/example/example.xls"
                , "application/vnd.ms-excel"
                , new ClassPathResource("data/example_xls/example.xls").getInputStream());
        when(fileJobService.save(any(FileJob.class))).thenReturn(fileJob);
        ResultActions response = mockMvc.perform(multipart("/" + urlFileController + urlFileImport)
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString(String.valueOf(fileJob.getJobId()))));
    }

    @Test
    void getResultOfImportingByJobID() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.IN_PROGRESS);
        fileJob.setType(FileJobType.IMPORT);
        when(fileJobService.getFileJob(any(Long.class))).thenReturn(Optional.of(fileJob));
        ResultActions response
                = mockMvc.perform(
                get("/" + urlFileController + urlFileImport + "/{id}"
                        , fileJob.getJobId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString("IN PROGRESS")));
    }

    @Test
    void export() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.IN_PROGRESS);
        fileJob.setType(FileJobType.IMPORT);
        when(fileJobService.save(any(FileJob.class))).thenReturn(fileJob);
        ResultActions response = mockMvc.perform(get("/" + urlFileController + urlFileExport)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString(String.valueOf(fileJob.getJobId()))));
    }

    @Test
    void exportProgress() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.IN_PROGRESS);
        fileJob.setType(FileJobType.IMPORT);
        when(fileJobService.getFileJob(any(Long.class))).thenReturn(Optional.of(fileJob));
        ResultActions response = mockMvc.perform(
                get("/" + urlFileController + urlFileExport + "/{id}"
                        , fileJob.getJobId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString("IN PROGRESS")));
    }

    @Test
    void fileByJobIDWhenJobStatusError() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.ERROR);
        fileJob.setType(FileJobType.IMPORT);
        when(fileJobService.getFileJob(any(Long.class))).thenReturn(Optional.of(fileJob));
        mockMvc.perform(
                        get("/" + urlFileController + urlFileExport+"/{id}/file"
                                , fileJob.getJobId())
                                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print()).andExpect(status().isForbidden());
    }

    @Test
    void fileByJobIDWhenJobStatusDone() throws Exception {
        fileJob = new FileJob();
        fileJob.setJobId(1);
        fileJob.setStatus(FileJobStatus.DONE);
        fileJob.setType(FileJobType.IMPORT);
        fileJob.setFilePath(testXlsFile);
        Resource resource = null;
        try {
            Path filePath = Path.of(fileJob.getFilePath());
            resource = new UrlResource(filePath.toUri());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        when(fileJobService.getFileJob(any(Long.class))).thenReturn(Optional.of(fileJob));
        when(fileService.getByJobID(any(FileJob.class))).thenReturn(resource);
        mockMvc.perform(get("/"+urlFileController+urlFileExport+"/{id}/file", fileJob.getJobId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

}
