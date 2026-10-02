package com.practice.demo.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
public class OrderResponseDto {

    private UUID orderId;

    private UUID customerId;

    private List<OrderItemResponseDto> items;

    private BigDecimal totalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
