package com.practice.demo.repository;

import com.practice.demo.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepo extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAll();
}
