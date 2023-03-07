package com.example.mzrt.controller;

import com.example.mzrt.model.User;
import com.example.mzrt.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void gettingUsers_withoutAuthentication_shouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingUsers_withAuthentication_shouldReturnPageOfUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingRegistrationFormTest() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/new"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void savingUser_shouldRedirectToUserListPage() throws Exception {
        when(userService.saveUser(any(), any())).thenReturn(User.builder().build());

        mockMvc.perform(post("/users")
                        .flashAttr("user", User.builder().build())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingUpdateFormTest() throws Exception {
        when(userService.findById(anyInt())).thenReturn(User.builder().build());

        mockMvc.perform(get("/users/{id}/updating", anyInt()))
                .andExpect(status().isOk())
                .andExpect(view().name("users/update"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deleting_shouldRedirectToPageOfOwners() throws Exception {
        doNothing().when(userService).deleteById(anyInt());

        mockMvc.perform(delete("/users/{id}", anyInt()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }
}
