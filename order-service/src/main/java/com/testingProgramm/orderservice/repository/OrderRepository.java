package com.testingProgramm.orderservice.repository;

import com.testingProgramm.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
