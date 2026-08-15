package com.learnai.repository;

import com.learnai.entity.ModelOrder;
import com.learnai.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface ModelOrderRepository extends JpaRepository<ModelOrder, Long> {

    List<ModelOrder> findByUserIdOrderByOrderDateDesc(Long userId);

    long countByStatus(OrderStatus status);

    @Query("select coalesce(sum(o.totalAmount), 0) from ModelOrder o where o.status = com.learnai.entity.enums.OrderStatus.Completed")
    BigDecimal sumCompletedAmount();
}
