package com.vora.backend.product.dto.Admin_dto.request;

import com.vora.backend.enums.OrderStatus;
import lombok.Data;
@Data
public class OrderStatusRequest {
    private OrderStatus status;
}
