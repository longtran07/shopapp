package com.project.shopapp.services;

import com.project.shopapp.dtos.CartItemDTO;
import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.*;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional
    public Order ceaterOrder(OrderDTO orderDTO) throws Exception {
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
        order.setOrderDate(LocalDate.now()); //  real time
        order.setStatus(OrderStatus.PENDING);
        // Kiểm tra shippinDate phải lớn hơn ngày hnay

        LocalDate shippingDate = orderDTO.getShippingDate()== null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        order.setTotalMoney(order.getTotalMoney());
        orderRepository.save(order);

        // create list orderdetail by cartIteamDTO
        List<OrderDetail> orderDetails=new ArrayList<>();
        for(CartItemDTO cartItemDTO: orderDTO.getCartItems()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // lay thong tin tu cartItemDTO
            Long productId=cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();
            // tim thong tin treen db
            Product product=productRepository.findById(productId).orElseThrow(
                    ()-> new DateTimeException("Product not found with id = "+ productId)
            );
            // dat thong tin cho Orderdetail
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);

        return order;
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(long id, OrderDTO orderDTO) throws Exception {
        Order order = orderRepository.findById(id)
                .orElseThrow(()-> new DataNotFoundException("Cannot find order with id: "+id));
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(()-> new DataNotFoundException("Cannot find user with id: "+id));
        // tao luong anh xa de kiem soat anh xa
        modelMapper.typeMap(OrderDTO.class,Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //cap nhat cac truong cuar don hang tu orderDTO
        modelMapper.map(orderDTO,order);
        order.setUser(existingUser);

        return orderRepository.save(order);
    }

    @Override
    @Transactional
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

    @Override
    public Page<Order> getOrdersByKeyword(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
}
