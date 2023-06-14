package com.natlex.test_app.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.natlex.test_app.controller.UserController;
import com.natlex.test_app.model.entity.AppUser;
import com.natlex.test_app.service.IAppUserService;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class TestUserController {
    @MockBean
    private IAppUserService appUserService;
    @Autowired
    private MockMvc mockMvc;
    private AppUser appUser;
    @Autowired
    ObjectMapper mapper;
    @Value("${values.url.user.controller}")
    private String urlUserController;

    @Test
    void addUser() throws Exception {
        appUser = new AppUser();
        mapper = new ObjectMapper();
        appUser.setAppUserId(1);
        appUser.setLogin("user");
        appUser.setPassword("user");
        when(appUserService.save(any(AppUser.class))).thenReturn(appUser);
        ResultActions response = mockMvc.perform(post("/"+urlUserController)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(appUser))
                );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.app_user_id",
                        is((int)appUser.getAppUserId())))
                .andExpect(jsonPath("$.login",
                        is(appUser.getLogin())))
                .andExpect(jsonPath("$.password",
                        is(appUser.getPassword())));
    }

    @WithMockUser(value = "admin")
    @Test
    void getById() throws Exception {
        mapper = new ObjectMapper();
        appUser = new AppUser();
        appUser.setAppUserId(1);
        appUser.setLogin("user");
        appUser.setPassword("user");
        when(appUserService.getById(any(Long.class))).thenReturn(Optional.of(appUser));
        ResultActions response = mockMvc.perform(get("/"+urlUserController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .param("id", "1")
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.app_user_id",
                        is((int)appUser.getAppUserId())))
                .andExpect(jsonPath("$.login",
                        is(appUser.getLogin())))
                .andExpect(jsonPath("$.password",
                        is(appUser.getPassword())));
    }

    @WithMockUser(value = "admin")
    @Test
    void updateUser() throws Exception {
        appUser = new AppUser();
        mapper = new ObjectMapper();
        appUser.setAppUserId(1);
        appUser.setLogin("user");
        appUser.setPassword("user");
        when(appUserService.getById(any(Long.class))).thenReturn(Optional.of(appUser));
        when(appUserService.save(any(AppUser.class))).thenReturn(appUser);
        ResultActions response = mockMvc.perform(put("/"+urlUserController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(appUser))
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.app_user_id",
                        is((int)appUser.getAppUserId())))
                .andExpect(jsonPath("$.login",
                        is(appUser.getLogin())))
                .andExpect(jsonPath("$.password",
                        is(appUser.getPassword())));
    }

    @WithMockUser(value = "admin")
    @Test
    void deleteUser() throws Exception {
        appUser = new AppUser();
        mapper = new ObjectMapper();
        appUser.setAppUserId(1);
        appUser.setLogin("user");
        appUser.setPassword("user");
        when(appUserService.getById(any(Long.class))).thenReturn(Optional.of(appUser));
        ResultActions response = mockMvc.perform(delete("/"+urlUserController)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(appUser))
        );
        response.andDo(print()).
                andExpect(status().isOk())
                .andExpect(content().string(containsString("user removed")));
    }

}
