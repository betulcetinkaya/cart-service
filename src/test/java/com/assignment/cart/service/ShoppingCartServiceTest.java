package com.assignment.cart.service;


import com.assignment.cart.ServiceBaseTest;
import com.assignment.cart.domain.dto.CampaignDto;
import com.assignment.cart.domain.dto.CouponDto;
import com.assignment.cart.domain.dto.ProductDto;
import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;
import com.assignment.cart.exception.CouponNotFoundException;
import com.assignment.cart.exception.CouponNotValidException;
import com.assignment.cart.exception.ServiceExecutionException;
import com.assignment.cart.exception.ShoppingCartNotFoundException;
import com.assignment.cart.repository.IDeliveryServiceClient;
import com.assignment.cart.repository.IDiscountServiceClient;
import com.assignment.cart.repository.IProductServiceClient;
import com.assignment.cart.repository.ShoppingCartRepository;
import com.assignment.cart.service.impl.ShoppingCartServiceImpl;
import com.assignment.cart.testdata.ShoppingCartTestData;
import com.assignment.cart.utils.ValidationMessages;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartServiceTest extends ServiceBaseTest {

    @MockBean
    private ShoppingCartRepository shoppingCartRepository;

    @MockBean
    private IProductServiceClient iProductServiceClient;

    @MockBean
    private IDeliveryServiceClient iDeliveryServiceClient;

    @MockBean
    private IDiscountServiceClient iDiscountServiceClient;

    @Autowired
    private ShoppingCartServiceImpl shoppingCartService;

    @Test
    public void testAddItem_SendExistingShoppingCartItem_UpdateItemQuantity() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        ShoppingCartItem shoppingCartItem = ShoppingCartTestData.getShoppingCartItem();
        ProductDto productDto = ShoppingCartTestData.getProductDto();
        when(iProductServiceClient.getProduct(anyString())).thenReturn(productDto);
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));

        shoppingCartService.addItem(shoppingCart.getId(), shoppingCartItem);

        ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(shoppingCartArgumentCaptor.capture());
        ShoppingCart updatedShoppingCart = shoppingCartArgumentCaptor.getValue();
        Assert.assertEquals(2, updatedShoppingCart.getShoppingCartItems().size());
        Assert.assertEquals(updatedShoppingCart.getShoppingCartItems().get(0).getQuantity(), 9);
    }

    @Test
    public void testAddItem_SendNewShoppingCartItemWithoutCampaign_AddItem() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        ShoppingCartItem shoppingCartItem = ShoppingCartTestData.getShoppingCartItem();
        shoppingCartItem.setProductId("ITEM-003");
        ProductDto productDto = ShoppingCartTestData.getProductDto();
        when(iProductServiceClient.getProduct(anyString())).thenReturn(productDto);
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        when(iDeliveryServiceClient.calculateDeliveryCost(any(), anyInt(), anyInt())).thenReturn(new BigDecimal(10));

        shoppingCartService.addItem(shoppingCart.getId(), shoppingCartItem);

        ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(shoppingCartArgumentCaptor.capture());
        ShoppingCart updatedShoppingCart = shoppingCartArgumentCaptor.getValue();
        Assert.assertEquals(3, updatedShoppingCart.getShoppingCartItems().size());
        Assert.assertEquals(updatedShoppingCart.getShoppingCartItems().get(2).getQuantity(), 6);
        Assert.assertEquals(new BigDecimal(10), updatedShoppingCart.getDeliveryCost());
        ArgumentCaptor<Integer> numberOfDeliveries = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> numberOfProducts = ArgumentCaptor.forClass(Integer.class);
        verify(iDeliveryServiceClient).calculateDeliveryCost(any(), numberOfDeliveries.capture(), numberOfProducts.capture());
        int deliveryCount = numberOfDeliveries.getValue();
        int productCount = numberOfProducts.getValue();
        Assert.assertEquals(2, deliveryCount);
        Assert.assertEquals(14, productCount);
    }

    @Test
    public void testAddItem_SendNewShoppingCartItemWithCampaign_AddItem() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        ShoppingCartItem shoppingCartItem = ShoppingCartTestData.getShoppingCartItem();
        shoppingCartItem.setProductId("ITEM-003");
        ProductDto productDto = ShoppingCartTestData.getProductDto();
        CampaignDto campaignDto = ShoppingCartTestData.getCampaignDto();
        when(iProductServiceClient.getProduct(anyString())).thenReturn(productDto);
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        when(iDiscountServiceClient.getBestCampaign(anyString(), anyInt(), any(BigDecimal.class))).thenReturn(campaignDto);
        when(iDeliveryServiceClient.calculateDeliveryCost(any(), anyInt(), anyInt())).thenReturn(new BigDecimal(10));

        shoppingCartService.addItem(shoppingCart.getId(), shoppingCartItem);

        ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(shoppingCartArgumentCaptor.capture());
        ShoppingCart updatedShoppingCart = shoppingCartArgumentCaptor.getValue();
        Assert.assertEquals(3, updatedShoppingCart.getShoppingCartItems().size());
        Assert.assertEquals(updatedShoppingCart.getShoppingCartItems().get(2).getQuantity(), 6);
        Assert.assertEquals(new BigDecimal(10), updatedShoppingCart.getDeliveryCost());
        Assert.assertEquals(new BigDecimal(600), updatedShoppingCart.getShoppingCartItems().get(2).getAmount());
        Assert.assertEquals(new BigDecimal(550), updatedShoppingCart.getShoppingCartItems().get(2).getAmountAfterDiscounts());
        Assert.assertEquals(new BigDecimal(1000), updatedShoppingCart.getTotalAmount());
        Assert.assertEquals(new BigDecimal(880), updatedShoppingCart.getTotalAmountAfterDiscounts());
        Assert.assertEquals(new BigDecimal(120), updatedShoppingCart.getCampaignDiscount());
        ArgumentCaptor<Integer> numberOfDeliveries = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> numberOfProducts = ArgumentCaptor.forClass(Integer.class);
        verify(iDeliveryServiceClient).calculateDeliveryCost(any(), numberOfDeliveries.capture(), numberOfProducts.capture());
        int deliveryCount = numberOfDeliveries.getValue();
        int productCount = numberOfProducts.getValue();
        Assert.assertEquals(2, deliveryCount);
        Assert.assertEquals(14, productCount);
    }

    @Test
    public void testApplyCoupon_CouponNotFound_ThrowException() {
        String id = "cart";
        String couponId = "coupon";
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        thrown.expect(CouponNotFoundException.class);
        thrown.expectMessage(couponId);

        shoppingCartService.applyCoupon(id, couponId);

        verify(shoppingCartRepository).findById(anyString());
    }

    @Test
    public void testApplyCoupon_CouponNotValid_ThrowException() {
        String id = "cart";
        String couponId = "coupon";
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        CouponDto couponDto = ShoppingCartTestData.getCouponDto();
        couponDto.setMinAmount(new BigDecimal(2000));
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        when(iDiscountServiceClient.getCouponById(anyString())).thenReturn(couponDto);
        thrown.expect(CouponNotValidException.class);
        thrown.expectMessage(couponId);

        shoppingCartService.applyCoupon(id, couponId);

        verify(shoppingCartRepository).findById(anyString());
    }


    @Test
    public void testApplyCoupon_ValidCoupon_ApplyCoupon() {
        String id = "cart";
        String couponId = "coupon";
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        CouponDto couponDto = ShoppingCartTestData.getCouponDto();
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        when(iDiscountServiceClient.getCouponById(anyString())).thenReturn(couponDto);

        shoppingCartService.applyCoupon(id, couponId);

        verify(shoppingCartRepository).findById(anyString());
        ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(shoppingCartArgumentCaptor.capture());
        ShoppingCart updated = shoppingCartArgumentCaptor.getValue();
        Assert.assertEquals(couponId, updated.getCouponId());
        Assert.assertEquals(new BigDecimal(440).setScale(10), updated.getCouponDiscount());
        Assert.assertEquals(new BigDecimal(440).setScale(10), updated.getTotalAmountAfterDiscounts());
    }

    @Test
    public void testAddItem_ProductNotFound_ThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        ShoppingCartItem shoppingCartItem = ShoppingCartTestData.getShoppingCartItem();
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));
        thrown.expect(ServiceExecutionException.class);
        thrown.expectMessage(ValidationMessages.PRODUCT_NOT_FOUND);

        shoppingCartService.addItem(shoppingCart.getId(), shoppingCartItem);

        ArgumentCaptor<ShoppingCart> shoppingCartArgumentCaptor = ArgumentCaptor.forClass(ShoppingCart.class);
        verify(shoppingCartRepository).save(shoppingCartArgumentCaptor.capture());
        ShoppingCart updatedShoppingCart = shoppingCartArgumentCaptor.getValue();
        Assert.assertEquals(3, updatedShoppingCart.getShoppingCartItems().size());
        Assert.assertEquals(updatedShoppingCart.getShoppingCartItems().get(2).getQuantity(), 6);
    }

    @Test
    public void testCreate_SendValidShoppingCart_ReturnShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(shoppingCart);

        ShoppingCart created = shoppingCartService.create(shoppingCart);

        verify(shoppingCartRepository).save(any(ShoppingCart.class));
        Assert.assertEquals(shoppingCart.getId(), created.getId());
        Assert.assertEquals(shoppingCart.getShoppingCartItems().size(), created.getShoppingCartItems().size());
    }

    @Test
    public void testGetById_RecordNotFound_ThrowException() {
        String id = "PRODUCT-001";
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.empty());
        thrown.expect(ShoppingCartNotFoundException.class);
        thrown.expectMessage(id);

        shoppingCartService.getById(id);

        verify(shoppingCartRepository).findById(anyString());
    }

    @Test
    public void testGetBy_SendExistingId_ReturnShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestData.getShoppingCart();
        when(shoppingCartRepository.findById(anyString())).thenReturn(Optional.of(shoppingCart));

        ShoppingCart found = shoppingCartService.getById(shoppingCart.getId());

        verify(shoppingCartRepository).findById(anyString());
        Assert.assertEquals(shoppingCart.getId(), found.getId());
        Assert.assertEquals(shoppingCart.getShoppingCartItems().size(), found.getShoppingCartItems().size());
    }


}
