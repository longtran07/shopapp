package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;

import java.util.List;

public interface IOrderService {
    Order ceaterOrder(OrderDTO orderDTO) throws DataNotFoundException;

    Order getOrder(Long id);
    Order updateOrder(long id, OrderDTO orderDTO);
    void deleteOrder(long id);
    List<Order> findByUserId(Long userId);
}
