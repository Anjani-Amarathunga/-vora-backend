package com.vora.backend.product.dto.Admin_dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class StockUpdateRequest {
    @NotNull @Min(0) private Integer stockQty;
}
