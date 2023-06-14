package com.natlex.test_app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.CoreMatchers.is;
import com.natlex.test_app.controller.GeologicalController;
import static org.mockito.ArgumentMatchers.*;
import com.natlex.test_app.model.entity.Geological;
import com.natlex.test_app.service.IGeologicalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import java.util.Optional;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(value = "admin")
@WebMvcTest(GeologicalController.class)
public class TestGeologicalController {
    @MockBean
    private IGeologicalService geologicalService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private Geological geological;
    @Value("${values.url.geological.controller}")
    private String urlGeologicalController;

    @Test
    void addGeological() throws Exception {
        geological = new Geological();
        mapper = new ObjectMapper();
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        given(geologicalService.save(any(Geological.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        ResultActions response = mockMvc.perform(post("/"+urlGeologicalController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(geological)));
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name",
                        is(geological.getName())))
                .andExpect(jsonPath("$.code",
                        is(geological.getCode())));
    }

    @Test
    void getById() throws Exception {
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        when(geologicalService.getById(any(Long.class))).thenReturn(Optional.of(geological));
        ResultActions response = mockMvc.perform(get("/"+urlGeologicalController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("id", "1")
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name",
                        is(geological.getName())))
                .andExpect(jsonPath("$.code",
                        is(geological.getCode())));
    }


    @Test
    void updateGeological() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        when(geologicalService.getById(any(Long.class))).thenReturn(Optional.of(geological));
        given(geologicalService.save(any(Geological.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        ResultActions response = mockMvc.perform(put("/"+urlGeologicalController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(geological)));

        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.geological_id",
                        is(Integer.valueOf((int) geological.getGeologicalId()))));
    }


    @Test
    void deleteGeological() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        when(geologicalService.getById(any(Long.class))).thenReturn(Optional.of(geological));
        ResultActions response = mockMvc.perform(delete("/"+urlGeologicalController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(geological)));
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString("geological object removed")));
    }
}
