package com.practice.demo.service;

import com.practice.demo.dao.OrderItemRequestDto;
import com.practice.demo.dao.OrderItemResponseDto;
import com.practice.demo.dao.OrderRequestDto;
import com.practice.demo.dao.OrderResponseDto;
import com.practice.demo.entity.Order;
import com.practice.demo.entity.OrderItem;
import com.practice.demo.entity.Product;
import com.practice.demo.order.OrderStatus;
import com.practice.demo.repository.OrderRepo;
import com.practice.demo.repository.ProductRepo;
import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final ProductRepo productRepo;

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders(){
         List<Order> orders = orderRepo.findAll();
         List<OrderResponseDto> ordersResponseDto = new ArrayList<>();
         for(Order order: orders){
             OrderResponseDto orderResponseDto = toOrderResponse(order);
             ordersResponseDto.add(orderResponseDto);
         }

         return ordersResponseDto;
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(String orderId){
        UUID id = UUID.fromString(orderId);
        Order order = orderRepo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderRequest){
        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for(OrderItemRequestDto orderItemRequestDto: orderRequest.getItems()){
            Product product = productRepo.findById(orderItemRequestDto.getProductId()).orElseThrow(() -> new RuntimeException("Product Not Found"));
            if (product.getStockQuantity() < orderItemRequestDto.getQuantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            product.setStockQuantity(
                    product.getStockQuantity() - orderItemRequestDto.getQuantity()
            );

            BigDecimal subTotal = product.getPrice().multiply((BigDecimal.valueOf(orderItemRequestDto.getQuantity())));
            OrderItem orderItem = new OrderItem();
            orderItem.setUnitPrice(product.getPrice());
            orderItem.setProduct(product);
            orderItem.setSubTotal(subTotal);
            order.addItem(orderItem);
            orderItem.setQuantity(orderItemRequestDto.getQuantity());
            totalAmount = totalAmount.add(subTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setCustomerId(orderRequest.getCustomerId());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        orderRepo.save(order);
        return toOrderResponse(order);
    }

    private OrderResponseDto toOrderResponse(Order order){
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderId(order.getOrderId());
        orderResponseDto.setCustomerId(order.getCustomerId());
        orderResponseDto.setTotalAmount(order.getTotalAmount());

        List<OrderItemResponseDto> orderItemsResponseDto = new ArrayList<>();
        for(OrderItem orderItem: order.getItems()){
            OrderItemResponseDto orderItemResponseDto = toOrderResponseItem(orderItem);
            orderItemsResponseDto.add(orderItemResponseDto);
        }
        orderResponseDto.setItems(orderItemsResponseDto);

        orderResponseDto.setUpdatedAt(order.getUpdatedAt());
        orderResponseDto.setCreatedAt(order.getCreatedAt());
        return orderResponseDto;
    }

    private OrderItemResponseDto toOrderResponseItem(OrderItem orderItem){
        OrderItemResponseDto orderItemResponseDto = new OrderItemResponseDto();
        orderItemResponseDto.setProductId(orderItem.getProduct().getProductId());
        orderItemResponseDto.setQuantity(orderItem.getQuantity());
        orderItemResponseDto.setSubtotal(orderItem.getSubTotal());
        orderItemResponseDto.setProductName(orderItem.getProduct().getName());
        orderItemResponseDto.setUnitPrice(orderItem.getUnitPrice());
        return orderItemResponseDto;
    }

}
