package com.assignment.cart.testdata;

import com.assignment.cart.domain.dto.CampaignDto;
import com.assignment.cart.domain.dto.CategoryDto;
import com.assignment.cart.domain.dto.CouponDto;
import com.assignment.cart.domain.dto.ProductDto;
import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;
import com.assignment.cart.domain.enums.DiscountType;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ShoppingCartTestData {

    public static ShoppingCart getShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId("SHOPPING-CART-001");
        shoppingCart.setShoppingCartItems(new ArrayList<>());
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setProductId("ITEM-001");
        shoppingCartItem.setQuantity(3);
        shoppingCartItem.setAmount(new BigDecimal(100));
        shoppingCartItem.setAmountAfterDiscounts(new BigDecimal(80));
        shoppingCartItem.setCategoryId("CATEGORY-001");
        shoppingCart.getShoppingCartItems().add(shoppingCartItem);
        ShoppingCartItem shoppingCartItem2 = new ShoppingCartItem();
        shoppingCartItem2.setProductId("ITEM-002");
        shoppingCartItem2.setQuantity(5);
        shoppingCartItem2.setCategoryId("CATEGORY-002");
        shoppingCartItem2.setAmount(new BigDecimal(300));
        shoppingCartItem2.setAmountAfterDiscounts(new BigDecimal(250));
        shoppingCart.getShoppingCartItems().add(shoppingCartItem2);
        shoppingCart.setTotalAmount(new BigDecimal(1000));
        shoppingCart.setTotalAmountAfterDiscounts(new BigDecimal(880));
        return shoppingCart;
    }

    public static ShoppingCartItem getShoppingCartItem() {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setProductId("ITEM-001");
        shoppingCartItem.setQuantity(6);
        return shoppingCartItem;
    }

    public static ProductDto getProductDto() {
        ProductDto productDto = new ProductDto();
        productDto.setId("ITEM-001");
        productDto.setCategory(getCategoryDto());
        productDto.setPrice(new BigDecimal(100));
        productDto.setTitle("ITEM-TITLE-001");
        return productDto;
    }

    public static CategoryDto getCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId("CATEGORY-001");
        categoryDto.setParentId(null);
        categoryDto.setTitle("CATEGORY-TITLE-001");
        return categoryDto;
    }

    public static CouponDto getCouponDto() {
        CouponDto couponDto = new CouponDto();
        couponDto.setDiscount(new BigDecimal(50));
        couponDto.setDiscountType(DiscountType.RATE);
        couponDto.setId("coupon");
        couponDto.setMinAmount(new BigDecimal(100));
        return couponDto;
    }

    public static CampaignDto getCampaignDto() {
        CampaignDto campaignDto = new CampaignDto();
        campaignDto.setId("campaign");
        campaignDto.setCategoryId("CATEGORY-001");
        campaignDto.setDiscount(new BigDecimal(50));
        campaignDto.setDiscountType(DiscountType.AMOUNT);
        campaignDto.setMinQuantity(2);
        return campaignDto;
    }
}
