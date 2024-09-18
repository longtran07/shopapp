package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class   OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1,message = "Order's Id must be > 0 ")
    private long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "Product's Id must be > 0 ")
    private long productId;

    @Min(value = 0 ,message = "Price must be >= 0")
    private Float price;

    @JsonProperty("number_of_product")
    @Min( value =  1 , message = "number of product >=1")
    private int numberOfProduct;

    @JsonProperty("total_money")
    @Min(value = 0,message = "Total money >= 0")
    private Float totalMoney;

    private String color;
}
