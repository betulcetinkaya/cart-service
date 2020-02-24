package com.assignment.cart.controller;

import com.assignment.cart.ControllerBaseTest;
import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;
import com.assignment.cart.service.ShoppingCartService;
import com.assignment.cart.testdata.ShoppingCartTestData;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@WebMvcTest(ShoppingCartController.class)
public class ShoppingCartControllerTestBaseTest extends ControllerBaseTest {

    @MockBean
    private ShoppingCartService shoppingCartService;

    @BeforeClass
    public static void init() {
        baseAddress = "/shopping-carts";
    }

    @Test
    public void testCreateShoppingCart_SendValidRequest_ReturnCreated() throws Exception {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartService.create(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ResultActions perform = mockMvc.perform(post(baseAddress)
                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(shoppingCart)));


        perform.andExpect(status().isCreated());
    }

    @Test
    public void testCreateShoppingCartItem_SendValidRequest_ReturnCreated() throws Exception {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        ShoppingCartItem shoppingCartItem = ShoppingCartTestData.getShoppingCartItem();
        when(shoppingCartService.addItem(anyString(), any(ShoppingCartItem.class))).thenReturn(shoppingCart);

        ResultActions perform = mockMvc.perform(post(baseAddress + "/"+ shoppingCart.getId() +"/items")
                .contentType(MediaType.APPLICATION_JSON).content(asJsonString(shoppingCartItem)));


        perform.andExpect(status().isOk());
    }

    @Test
    public void testApplyCoupon_SendValidRequest_ApplyCoupon() throws Exception {
        String couponId = "coupon-001";
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartService.addItem(anyString(), any(ShoppingCartItem.class))).thenReturn(shoppingCart);

        mockMvc.perform(MockMvcRequestBuilders.put(baseAddress + "/"+ shoppingCart.getId() +"/coupon/"+couponId)
                .contentType(mediaType)
                .content(asJsonString(null)))
                .andExpect(status().isOk());
    }

    @Test
    public void testCalculateDelivery_SendValidRequest_CalculateDelivery() throws Exception {
        String deliveryId = "delivery-001";
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartService.calculateDelivery(anyString(), anyString())).thenReturn(shoppingCart);

        mockMvc.perform(MockMvcRequestBuilders.put(baseAddress + "/"+ shoppingCart.getId() +"/delivery/"+deliveryId)
                .contentType(mediaType)
                .content(asJsonString(null)))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetShoppingCart_SendId_GetAShoppingCart() throws Exception {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartService.getById(shoppingCart.getId())).thenReturn(shoppingCart);

        ResultActions resultActions = mockMvc.perform(get(baseAddress + "/" + shoppingCart.getId())
                .contentType(mediaType));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(shoppingCart.getId())));

        verify(shoppingCartService).getById(anyString());
    }


}
