package com.arwka.openapiedu.ui.controller;

import com.arwka.openapiedu.service.OrdersService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openapitools.model.EditedOrder;
import org.openapitools.model.NewOrder;
import org.openapitools.model.Order;
import org.openapitools.model.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  OrdersService ordersService;

  @Test
  @DisplayName("deleteOrder without auth should return 401 Unauthorized")
  void unauthorizedTest() throws Exception {
    mockMvc.perform(delete("/orders/1"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("getOrdersWithoutParams should return correct order list.")
  void getOrdersTest() throws Exception {
    Order order1 = new Order();
    Order order2 = new Order();
    order1.setProductId(15L);
    order2.setId(5L);

    when(ordersService.getOrdersWithoutParams())
        .thenReturn(List.of(order1, order2));

    mockMvc.perform(get("/orders")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.[0].productId", is(15)))
        .andExpect(jsonPath("$.[1].id", is(5)));
  }

  @Test
  @DisplayName("createNewOrder should create and return new order")
  void postOrdersTest() throws Exception {

    Order order = new Order();
    order.setId(4L);

    String jsonToPost = "{ \"id\": 4,\"productId\": 5, \"quantity\": 10 }";

    when(ordersService.createNewOrder(any(NewOrder.class)))
        .thenReturn(order);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonToPost))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(4)));

  }

  @Test
  @DisplayName("deleteOrder should delete order and return isNoContent")
  @WithMockUser("ADMIN")
  void deleteOrderTest() throws Exception {
    mockMvc.perform(delete("/orders/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("patchOrder should patch order by json")
  @WithMockUser("ADMIN")
  void patchOrderTest() throws Exception {

    String jsonToPost = "{ \"quantity\": 75, \"complete\": true}";

    when(ordersService.patchOrder(anyString(), any(EditedOrder.class)))
        .thenReturn(new Order());

    mockMvc.perform(patch("/orders/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonToPost))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("approveOrder & DeliverOrder should change StatusEnum of order to approved")
  @WithMockUser("ADMIN")
  void putApprovedDeliveredTest() throws Exception {

    Order order = new Order();
    order.setStatus(StatusEnum.APPROVED);

    when(ordersService.approveOrder("1"))
        .thenReturn(order);
    System.out.println(ordersService.approveOrder("1"));

    mockMvc.perform(put("/orders/1/approved"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("approved")));

    order.setStatus(StatusEnum.DELIVERED);

    when(ordersService.deliverOrder("1"))
        .thenReturn(order);

    mockMvc.perform(put("/orders/1/approved"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status", is("delivered")));

  }

}
