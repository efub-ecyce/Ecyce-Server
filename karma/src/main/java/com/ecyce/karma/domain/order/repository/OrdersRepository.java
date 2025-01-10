package com.ecyce.karma.domain.order.repository;

import com.ecyce.karma.domain.order.entity.Orders;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    @Query("select o from Orders o where o.product.productId =:productId")
    List<Orders> findByProductId(@Param("productId")Long productId);

    // 주문 ID로만 조회 (존재 여부 확인)
    @Query("select o from Orders o where o.orderId = :orderId")
    Optional<Orders> findByOrderId(@Param("orderId") Long orderId);

    // 구매 내역 조회 (특정 구매자의 전체 주문)
    @Query("select o from Orders o where o.buyerUser.userId = :userId")
    List<Orders> findAllByBuyerUserId(@Param("userId") Long userId);

    // 판매 내역 조회 (특정 판매자의 전체 주문)
    @Query("select o from Orders o where o.sellerUser.userId = :userId")
    List<Orders> findAllBySellerUserId(@Param("userId") Long userId);

    @Query("select o from Orders o where o.buyerUser.userId = :userId or o.sellerUser.userId = :userId")
    List<Orders> findAllOrdersByUserId(@Param("userId") Long userId);
}
