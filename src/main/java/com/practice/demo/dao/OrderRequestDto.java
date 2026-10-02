package com.practice.demo.dao;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {
    @NotNull
    private UUID customerId;

    @NotEmpty
    @Valid
    private List<OrderItemRequestDto> items;
}
