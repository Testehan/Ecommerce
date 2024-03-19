package com.testehan.ecommerce.frontend.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testehan.ecommerce.frontend.order.dto.OrderReturnRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderRestControllerTests {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public OrderRestControllerTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        super();
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @WithUserDetails("tdan89@yahoo.com") // To solve Assertion Error : 302
    public void testSendOrderReturnRequestFailed() throws Exception {

        var orderId = 1111;
        var returnRequest = new OrderReturnRequest(orderId, "", "");

        var requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithUserDetails("tdan89@yahoo.com")
    public void testSendOrderReturnRequestSuccessful() throws Exception {

        var orderId = 18;
        var reason = "I bought the wrong items";
        var note = "I want my money !!!";

        var returnRequest = new OrderReturnRequest(orderId, reason, note);

        var requestURL = "/orders/return";

        mockMvc.perform(post(requestURL)
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
