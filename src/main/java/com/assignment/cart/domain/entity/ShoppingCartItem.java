package com.assignment.cart.domain.entity;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

public class ShoppingCartItem {

    @NotEmpty(message = "{NotEmpty.ShoppingCartProduct.productId}")
    private String productId;

    private String categoryId;

    private int quantity;

    private BigDecimal amount;

    private BigDecimal amountAfterDiscounts;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmountAfterDiscounts() {
        return amountAfterDiscounts;
    }

    public void setAmountAfterDiscounts(BigDecimal amountAfterDiscounts) {
        this.amountAfterDiscounts = amountAfterDiscounts;
    }
}
