package com.example.mzrt.controller;

import com.example.mzrt.model.Alert;
import com.example.mzrt.model.User;
import com.example.mzrt.service.AlertService;
import com.example.mzrt.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AlertService alertService;

    @Test
    public void gettingAlerts_withoutAuthentication_shouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/alerts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingListOfUserAlerts() throws Exception {
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingFormOfNewAlert() throws Exception {
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void afterSavingShouldRedirectToListOfUserAlerts() throws Exception {
        when(alertService.save(any())).thenReturn(Alert.builder().build());

        mockMvc.perform(post("/alerts")
                        .flashAttr("alert", Alert.builder().build())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/alerts/**"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingUpdateFormTest() throws Exception {
//        when(alertService.findById(anyInt())).thenReturn(Alert.builder().build());
//        when(userService.findById(anyInt())).thenReturn(User.builder().build());
//
//        mockMvc.perform(get("/alerts/updating/{id}", anyInt()))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("alert"))
//                .andExpect(view().name("alerts/update"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deleting_shouldRedirectToPageOfAlerts() throws Exception {

        when(alertService.findById(anyInt())).thenReturn(Alert.builder().build());
        doNothing().when(alertService).deleteById(anyInt());

        mockMvc.perform(delete("/alerts/{id}", anyInt()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/alerts/**"));
    }
}
