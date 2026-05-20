package com.vora.backend.repository.Admin_repository;

import com.vora.backend.user.Admin_entity.Order;
import com.vora.backend.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserId(Long userId, Pageable pageable);
    List<Order> findByStatus(OrderStatus status);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status <> :cancelled")
    long countActiveOrders(@Param("cancelled") OrderStatus cancelled);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.status = :delivered")
    BigDecimal totalRevenue(@Param("delivered") OrderStatus delivered);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
           "WHERE o.status = :delivered AND o.createdAt BETWEEN :start AND :end")
    BigDecimal revenueByDateRange(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end,
                                  @Param("delivered") OrderStatus delivered);

    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT MONTH(o.createdAt) as month, SUM(o.totalAmount) as revenue " +
           "FROM Order o WHERE YEAR(o.createdAt) = :year AND o.status = :delivered " +
           "GROUP BY MONTH(o.createdAt) ORDER BY month")
    List<Object[]> monthlyRevenue(@Param("year") int year,
                                  @Param("delivered") OrderStatus delivered);

    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();
}
