package com.assignment.cart.service;

import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;

public interface ShoppingCartService {

    ShoppingCart addItem(String id, ShoppingCartItem shoppingCartItem);

    ShoppingCart applyCoupon(String id, String couponId);

    ShoppingCart create(ShoppingCart shoppingCart);

    ShoppingCart getById(String id);

}
