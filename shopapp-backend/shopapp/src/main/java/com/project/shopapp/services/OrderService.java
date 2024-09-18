package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order ceaterOrder(OrderDTO orderDTO) throws DataNotFoundException {
        //Tìm xem userId có tồn tại kh ?
        User user=userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find user with id: " + orderDTO.getUserId()));
        // convert ordreDTO => Order
        // dungf modelmpaper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ OrderDTO
        Order order=new Order();
        modelMapper.map(orderDTO , order);
        order.setUser(user);
        order.setOrderDate(new Date()); //  real time
        order.setStatus(OrderStatus.PENDING);
        // Kiểm tra shippinDate phải lớn hơn ngày hnay

        LocalDate shippingDate = orderDTO.getShippingDate()== null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);

        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElseThrow(null);
    }

    @Override
    public Order updateOrder(long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new DateTimeException("Cannot find order with id: "+id));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DateTimeException("Cannot find user with id: "+id));
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO,order);

        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(long id) {
        Order order=orderRepository.findById(id).orElse(null);
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
