package com.assignment.cart.repository;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@FeignClient("delivery-service")
@RequestMapping("/deliveries")
public interface IDeliveryServiceClient {

    @RequestMapping
    BigDecimal calculateDeliveryCost(@RequestParam(value = "id") String id,
                                                     @RequestParam(value = "numberOfDeliveries") int numberOfDeliveries,
                                                     @RequestParam(value = "numberOfProducts") int numberOfProducts);
}
