package com.assignment.cart.repository;

import com.assignment.cart.domain.dto.CampaignDto;
import com.assignment.cart.domain.dto.CouponDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;

@FeignClient("discount-service")

public interface IDiscountServiceClient {

    @RequestMapping(value = "/campaigns", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    CampaignDto getBestCampaign(@RequestParam(value = "categoryId") String categoryId,
                                                @RequestParam(value = "quantity") int quantity,
                                                @RequestParam(value = "amount") BigDecimal amount);

    @RequestMapping(value = "/coupons/{id}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
    CouponDto getCouponById(@PathVariable("id") String id);
}
