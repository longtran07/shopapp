package com.project.shopapp.services.coupon;

import com.project.shopapp.models.Coupon;
import com.project.shopapp.models.CouponCondition;
import com.project.shopapp.repositories.CouponConditionRepository;
import com.project.shopapp.repositories.CouponRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class CouponService implements  ICouponService{
    private final CouponRepository couponRepository;
    private final CouponConditionRepository couponConditionRepository;

    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) {
        Coupon coupon=couponRepository.findByCode(couponCode)
                .orElseThrow(()-> new IllegalArgumentException("Coupon not found"));
        if(!coupon.isActive()){
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon,totalAmount);
        double finalAmount= totalAmount - discount;
        return finalAmount;
    }

    private double calculateDiscount(Coupon coupon, double totalAmount){
        List<CouponCondition> conditions = couponConditionRepository
                .findByCouponId(coupon.getId());
        double discount = 0.0;
        double updatedTotalAmount = totalAmount;  // Sử dụng tổng đơn hàng thực tế

        // Duyệt qua tất cả các điều kiện của coupon
        for (CouponCondition condition : conditions) {
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();
            double percentDiscount = Double.valueOf(String.valueOf(condition.getDiscountAmount()));  // Phần trăm giảm giá

            // Điều kiện giảm giá theo số tiền tối thiểu
            if (attribute.equals("minimum_amount")) {
                if (operator.equals(">") && updatedTotalAmount > Double.parseDouble(value)) {
                    discount += updatedTotalAmount * (percentDiscount / 100);
                }
            }
            // Điều kiện giảm giá theo ngày áp dụng
            else if (attribute.equals("applicable_date")) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase("BETWEEN") && currentDate.isEqual(applicableDate)) {
                    discount += updatedTotalAmount * (percentDiscount / 100);
                }
            }
        }

        // Trả về giá trị giảm giá đã tính
        return discount;
    }

}
