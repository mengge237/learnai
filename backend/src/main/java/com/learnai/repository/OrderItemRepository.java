package com.learnai.repository;

import com.learnai.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderIdOrderByOrderItemIdAsc(Long orderId);

    List<OrderItem> findByOrderIdIn(Collection<Long> orderIds);
}
