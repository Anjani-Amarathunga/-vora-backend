package com.vora.backend.product;

// Re-export Product from Admin_entity for convenience
public class Product extends com.vora.backend.user.Admin_entity.Product {
    
    public Integer getStock() {
        return this.getStockQty();
    }
}
