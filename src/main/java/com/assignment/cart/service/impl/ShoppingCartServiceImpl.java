package com.assignment.cart.service.impl;

import com.assignment.cart.domain.dto.CampaignDto;
import com.assignment.cart.domain.dto.CouponDto;
import com.assignment.cart.domain.dto.ProductDto;
import com.assignment.cart.domain.entity.ShoppingCart;
import com.assignment.cart.domain.entity.ShoppingCartItem;
import com.assignment.cart.domain.enums.DiscountType;
import com.assignment.cart.exception.CouponNotFoundException;
import com.assignment.cart.exception.CouponNotValidException;
import com.assignment.cart.exception.ServiceExecutionException;
import com.assignment.cart.exception.ShoppingCartNotFoundException;
import com.assignment.cart.repository.IDeliveryServiceClient;
import com.assignment.cart.repository.IDiscountServiceClient;
import com.assignment.cart.repository.IProductServiceClient;
import com.assignment.cart.repository.ShoppingCartRepository;
import com.assignment.cart.service.ShoppingCartService;
import com.assignment.cart.utils.ValidationMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private IProductServiceClient iProductServiceClient;

    @Autowired
    private IDiscountServiceClient iDiscountServiceClient;

    @Autowired
    private IDeliveryServiceClient iDeliveryServiceClient;

    @Override
    public ShoppingCart addItem(String id, ShoppingCartItem shoppingCartItem) {
        ShoppingCart shoppingCart = getById(id);
        ProductDto productDto = validateProductExist(shoppingCartItem);
        shoppingCartItem = addProductToShoppingCart(shoppingCart, shoppingCartItem, productDto);
        applyDiscounts(shoppingCartItem, productDto);
        calculateDeliveryCost(shoppingCart);
        calculateShoppingCartAmounts(shoppingCart);
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart applyCoupon(String id, String couponId) {
        ShoppingCart shoppingCart = getById(id);
        CouponDto couponDto = validateCoupon(shoppingCart, couponId);
        applyCouponDiscount(shoppingCart, couponDto);
        return shoppingCartRepository.save(shoppingCart);
    }

    private void applyDiscounts(ShoppingCartItem shoppingCartItem, ProductDto productDto) {
        CampaignDto bestCampaign = iDiscountServiceClient.getBestCampaign(productDto.getCategory().getId(), shoppingCartItem.getQuantity(), shoppingCartItem.getAmount());
        if (bestCampaign != null) {
            BigDecimal totalDiscount;
            if (DiscountType.RATE.equals(bestCampaign.getDiscountType())) {
                totalDiscount = shoppingCartItem.getAmount().multiply(bestCampaign.getDiscount()).divide(new BigDecimal(100), 10, RoundingMode.UP);
            } else {
                totalDiscount = bestCampaign.getDiscount();
            }
            shoppingCartItem.setAmountAfterDiscounts(shoppingCartItem.getAmount().subtract(totalDiscount));
        } else {
            shoppingCartItem.setAmountAfterDiscounts(shoppingCartItem.getAmount());
        }
    }

    private ProductDto validateProductExist(ShoppingCartItem shoppingCartItem) {
        ProductDto product = iProductServiceClient.getProduct(shoppingCartItem.getProductId());
        if (product == null) {
            throw new ServiceExecutionException(ValidationMessages.PRODUCT_NOT_FOUND);
        } else {
            return product;
        }
    }

    private ShoppingCartItem addProductToShoppingCart(ShoppingCart shoppingCart, ShoppingCartItem shoppingCartItem, ProductDto product) {
        Optional<ShoppingCartItem> optionalItem = shoppingCart.getShoppingCartItems().stream()
                .filter(item -> shoppingCartItem.getProductId().equals(item.getProductId()))
                .findFirst();
        if (optionalItem.isPresent()) {
            ShoppingCartItem existingItem = optionalItem.get();
            existingItem.setQuantity(existingItem.getQuantity() + shoppingCartItem.getQuantity());
            existingItem.setAmount(product.getPrice().multiply(new BigDecimal(existingItem.getQuantity())));
            existingItem.setCategoryId(product.getCategory().getId());
            return existingItem;
        } else {
            shoppingCartItem.setAmount(product.getPrice().multiply(new BigDecimal(shoppingCartItem.getQuantity())));
            shoppingCartItem.setCategoryId(product.getCategory().getId());
            shoppingCart.getShoppingCartItems().add(shoppingCartItem);
            return shoppingCartItem;
        }
    }

    private void calculateShoppingCartAmounts(ShoppingCart shoppingCart) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal campaignAmount = BigDecimal.ZERO;
        for(ShoppingCartItem item : shoppingCart.getShoppingCartItems()) {
            totalAmount = totalAmount.add(item.getAmount());
            campaignAmount = campaignAmount.add(item.getAmount().subtract(item.getAmountAfterDiscounts()));
        }
        BigDecimal totalAmountAfterDiscounts;
        totalAmountAfterDiscounts = totalAmount.subtract(campaignAmount);
        if (shoppingCart.getCouponDiscount() != null) {
            totalAmountAfterDiscounts = totalAmountAfterDiscounts.subtract(shoppingCart.getCouponDiscount());
        }
        shoppingCart.setTotalAmount(totalAmount);
        shoppingCart.setTotalAmountAfterDiscounts(totalAmountAfterDiscounts);
        shoppingCart.setCampaignDiscount(campaignAmount);
    }

    private void calculateDeliveryCost(ShoppingCart shoppingCart) {
        Map<String, List<ShoppingCartItem>> categories = shoppingCart.getShoppingCartItems().stream()
                .collect(groupingBy(ShoppingCartItem::getCategoryId));
        int numberOfDeliveries = categories.size();
        int numberOfProducts = shoppingCart.getShoppingCartItems().stream().mapToInt(item -> item.getQuantity()).sum();
        BigDecimal deliveryCost = iDeliveryServiceClient.calculateDeliveryCost(shoppingCart.getDeliveryId(), numberOfDeliveries, numberOfProducts);
        shoppingCart.setDeliveryCost(deliveryCost);
    }

    private CouponDto validateCoupon(ShoppingCart shoppingCart, String couponId) {
        CouponDto coupon = iDiscountServiceClient.getCouponById(couponId);
        if (coupon == null) {
            throw new CouponNotFoundException(couponId);
        }
        if (shoppingCart.getTotalAmountAfterDiscounts().compareTo(coupon.getMinAmount()) < 0) {
            throw new CouponNotValidException(couponId);
        }
        return coupon;
    }

    private void applyCouponDiscount(ShoppingCart shoppingCart, CouponDto coupon) {
        BigDecimal couponDiscount;
        if (DiscountType.RATE.equals(coupon.getDiscountType())) {
            couponDiscount = shoppingCart.getTotalAmountAfterDiscounts().multiply(coupon.getDiscount()).divide(new BigDecimal(100), 10, RoundingMode.UP);
        } else {
            couponDiscount = coupon.getDiscount();
        }
        shoppingCart.setCouponId(coupon.getId());
        shoppingCart.setCouponDiscount(couponDiscount);
        shoppingCart.setTotalAmountAfterDiscounts(shoppingCart.getTotalAmountAfterDiscounts().subtract(couponDiscount));
    }

    @Override
    public ShoppingCart create(ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCart getById(String id) {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepository.findById(id);
        if (!shoppingCart.isPresent()) {
            throw new ShoppingCartNotFoundException(id);
        }
        return shoppingCart.get();
    }


}
