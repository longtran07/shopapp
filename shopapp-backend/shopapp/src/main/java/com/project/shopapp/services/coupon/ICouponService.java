package com.project.shopapp.services.coupon;

import org.springframework.stereotype.Service;

@Service
public interface ICouponService {
    double calculateCouponValue(String couponCode, double totalAmount);
}
