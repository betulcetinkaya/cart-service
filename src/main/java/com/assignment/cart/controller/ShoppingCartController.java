package com.assignment.cart.controller;

import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;
import com.assignment.cart.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/shopping-carts")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService productService;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    ResponseEntity<ShoppingCart> create(@Valid @RequestBody ShoppingCart product) {
        return new ResponseEntity<>(productService.create(product), HttpStatus.CREATED);
    }

    @PostMapping(path = "/{id}/items", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    ResponseEntity<ShoppingCart> addItem(@PathVariable String id,
                                         @Valid @RequestBody ShoppingCartItem item) {
        return new ResponseEntity<>(productService.addItem(id, item), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}/coupon/{couponId}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    ResponseEntity<ShoppingCart> addItem(@PathVariable String id,
                                         @PathVariable String couponId) {
        return new ResponseEntity<>(productService.applyCoupon(id, couponId), HttpStatus.OK);
    }

    @PutMapping(path = "/{id}/delivery/{deliveryId}", produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    ResponseEntity<ShoppingCart> calculateDelivery(@PathVariable String id,
                                         @PathVariable String deliveryId) {
        return new ResponseEntity<>(productService.calculateDelivery(id, deliveryId), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    public
    @ResponseBody
    ResponseEntity<ShoppingCart> getById(@PathVariable("id") String id) {
        ShoppingCart product = productService.getById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }


}
