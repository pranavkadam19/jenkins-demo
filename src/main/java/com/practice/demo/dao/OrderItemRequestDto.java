package com.practice.demo.dao;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderItemRequestDto {

    @NotNull
    private UUID productId;

    @NotNull
    @Positive
    private Integer quantity;
}
