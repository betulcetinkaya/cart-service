package com.assignment.cart.repository;

import com.assignment.cart.domain.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("product-service")
@RequestMapping("/products")
public interface IProductServiceClient {

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    ProductDto getProduct(@RequestParam("id") String id);
}
