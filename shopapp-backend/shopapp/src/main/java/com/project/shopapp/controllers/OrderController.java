package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.Order;
import com.project.shopapp.services.IOrderService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity <?> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessage = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessage);
            }
            Order orderResponse=orderService.ceaterOrder(orderDTO);
            return ResponseEntity.ok(orderResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{user_id}")

    public ResponseEntity<?>getOrders(@Valid @PathVariable("user_id") Long userId){
        try{
            List<Order> orders=orderService.findByUserId(userId);
            return ResponseEntity.ok(orders);
        }
        catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")

    public ResponseEntity<?>getOrder(
            @Valid @PathVariable("id") Long orderId){
        try{
            Order existingOrder=orderService.getOrder(orderId);
            return ResponseEntity.ok(existingOrder);
        } catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    // công việc của admin
    public ResponseEntity<?> updateOrder(
            @Valid @PathVariable("id") Long id,
            @Valid @RequestBody OrderDTO orderDTO){

        try{
            Order order=orderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(order);
        } catch (Exception e){
            return  ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@Valid @PathVariable("id") Long id){
        // xoá mm => cập nhật trường active = flase
       orderService.deleteOrder(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_ORDER_SUCCESSFULLY));
    }



}
