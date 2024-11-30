package com.ecyce.karma.domain.review.repository;

import com.ecyce.karma.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository  extends JpaRepository<Review , Long> {
    @Query("select r from Review r where r.orders.orderId =:orderId")
    List<Review> findByOrderId(@Param("orderId") Long orderId);

    @Query("select count(r) > 0 from Review r where r.orders.orderId = :orderId")
    boolean existsByOrderId(@Param("orderId") Long orderId);

    // 기본 조회
    @Query("SELECT r FROM Review r WHERE r.orders.product.productId = :productId")
    List<Review> findByProductId(@Param("productId") Long productId);

    // 별점 높은 순 조회
    @Query("SELECT r FROM Review r WHERE r.orders.product.productId = :productId ORDER BY r.rating DESC")
    List<Review> findByProductIdOrderByRatingDesc(@Param("productId") Long productId);

    // 오래된 순 조회
    @Query("SELECT r FROM Review r WHERE r.orders.product.productId = :productId ORDER BY r.createdAt ASC")
    List<Review> findByProductIdOrderByCreatedAtAsc(@Param("productId") Long productId);

    // 최신 순 조회
    @Query("SELECT r FROM Review r WHERE r.orders.product.productId = :productId ORDER BY r.createdAt DESC")
    List<Review> findByProductIdOrderByCreatedAtDesc(@Param("productId") Long productId);

    List<Review> findByOrders_Product_ProductId(Long productId);
}
