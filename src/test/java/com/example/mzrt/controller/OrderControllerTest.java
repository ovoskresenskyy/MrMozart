package com.example.mzrt.controller;

import com.example.mzrt.service.OrderService;
import com.example.mzrt.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserService userService;

    @Test
    public void gettingOrders_withoutAuthentication_shouldRedirectToLoginPage() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingOrders_withAuthentication_withoutUerId_shouldRedirectUserList() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void gettingOrders_withAuthentication_shouldReturnPageOfUserOrders() throws Exception {
//        List<Order> orders = new ArrayList<>();
//        orders.add(Order.builder().build());
//
//        when(userService.findById(anyInt())).thenReturn(User.builder().build());
//        when(orderService.findByUserId(anyInt())).thenReturn(orders);
//
//        mockMvc.perform(get("/orders/{userId}/{strategy}", anyInt(), Strategy.MOZART.name().toLowerCase()))
//                .andExpect(status().isOk())
//                .andExpect(view().name("orders/list"))
//                .andExpect(model().attributeExists("user"))
//                .andExpect(model().attributeExists("orders"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deletingOrders_withAuthentication_shouldReturnEmptyListOfOrders() throws Exception {

//        doNothing().when(orderService).deleteOrdersByUserId(anyInt());
//
//        mockMvc.perform(delete("/orders/{id}", anyInt()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrlPattern("/orders/**"));
    }

}
