package com.natlex.test_app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natlex.test_app.controller.SectionController;
import com.natlex.test_app.model.entity.Geological;
import com.natlex.test_app.model.entity.Section;
import com.natlex.test_app.service.ISectionService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(value = "admin")
@WebMvcTest(SectionController.class)
public class TestSectionController {
    @MockBean
    private ISectionService sectionService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private Section section;
    private Geological geological;
    private List<Section> sectionList;
    private List<Geological> geologicalList;
    @Value("${values.url.section.controller}")
    private String urlSectionController;
    @Value("${values.url.section.by_code}")
    private String urlSectionByGeologicalCode;

    @Test
    void addSection() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        section = new Section();
        section.setSectionId(1);
        section.setName("Section 1");
        geologicalList = new ArrayList<>();
        geologicalList.add(geological);
        section.setGeologicalClasses(geologicalList);
        String jsonSection = mapper.writeValueAsString(section);
        given(sectionService.save(any(Section.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));
        ResultActions response = mockMvc.perform(post("/"+urlSectionController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonSection));
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.section_id",
                        is((int)section.getSectionId())))
                .andExpect(jsonPath("$.name",
                        is(section.getName())))
                .andExpect(jsonPath("$.geologicalClasses").isArray())
                .andExpect(jsonPath("$.geologicalClasses", hasSize(1)))
        ;
    }

    @Test
    void getByGeologicalCode() throws Exception {
        sectionList = new ArrayList<>();
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        section = new Section();
        section.setSectionId(1);
        section.setName("Section 1");
        geologicalList = new ArrayList<>();
        geologicalList.add(geological);
        section.setGeologicalClasses(geologicalList);
        sectionList.add(section);
        when(sectionService.getByGeologicalCode(any(String.class))).thenReturn(sectionList);
        ResultActions response = mockMvc.perform(get("/"+urlSectionController+urlSectionByGeologicalCode)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("code", "GC11")
        );
        response.andDo(print()).
                andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        section = new Section();
        section.setSectionId(1);
        section.setName("Section 1");
        geologicalList = new ArrayList<>();
        geologicalList.add(geological);
        section.setGeologicalClasses(geologicalList);
        when(sectionService.getById(any(Long.class))).thenReturn(Optional.of(section));
        ResultActions response = mockMvc.perform(get("/"+urlSectionController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("id", "1")
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.name",
                        is(section.getName())))
                .andExpect(jsonPath("$.section_id",
                        is((int)section.getSectionId())))
                .andExpect(jsonPath("$.geologicalClasses").isArray())
                .andExpect(jsonPath("$.geologicalClasses", hasSize(1)))
        ;
    }

    @Test
    void updateSection() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        section = new Section();
        section.setSectionId(1);
        section.setName("Section 1");
        geologicalList = new ArrayList<>();
        geologicalList.add(geological);
        section.setGeologicalClasses(geologicalList);
        String jsonSection = mapper.writeValueAsString(section);

        when(sectionService.getById(any(Long.class))).thenReturn(Optional.of(section));
        given(sectionService.save(any(Section.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/"+urlSectionController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(jsonSection));
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.section_id",
                        is((int)section.getSectionId())))
                .andExpect(jsonPath("$.name",
                        is(section.getName())))
                .andExpect(jsonPath("$.geologicalClasses").isArray())
                .andExpect(jsonPath("$.geologicalClasses", hasSize(1)))
        ;
    }

    @Test
    void deleteSection() throws Exception {
        mapper = new ObjectMapper();
        geological = new Geological();
        geological.setGeologicalId(1);
        geological.setName("Geo Class 11");
        geological.setCode("GC11");
        section = new Section();
        section.setSectionId(1);
        section.setName("Section 1");
        geologicalList = new ArrayList<>();
        geologicalList.add(geological);
        section.setGeologicalClasses(geologicalList);
        when(sectionService.getById(any(Long.class))).thenReturn(Optional.of(section));
        ResultActions response = mockMvc.perform(delete("/"+urlSectionController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(section)));
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString("section object removed")));
    }
}
