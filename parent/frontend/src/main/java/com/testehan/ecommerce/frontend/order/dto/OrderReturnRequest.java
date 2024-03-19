package com.testehan.ecommerce.frontend.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class OrderReturnRequest {
    private Integer orderId;
    private String reason;
    private String note;
}
