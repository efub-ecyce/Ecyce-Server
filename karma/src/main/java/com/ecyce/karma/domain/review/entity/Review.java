package com.ecyce.karma.domain.review.entity;

import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewId")
    private Long reviewId;

    @Column(nullable = false)
    private String content;


    @Column(nullable = false)
    private Integer rating;

    @ManyToOne
    @JoinColumn(name = "userId" ,updatable = false, nullable = false)
    private User user;

    @OneToOne
    @JoinColumn(name = "orderId" ,unique = true , updatable = false, nullable = false)
    private Orders orders;

    @Builder
    public Review (String content , Integer rating , User user , Orders orders ){
        this.content = content;
        this.rating = rating;
        this.user = user;
        this.orders = orders;
    }

    public static Review createReview(String content, Integer rating, User user, Orders order) {
        return Review.builder()
                .content(content)
                .rating(rating)
                .user(user)
                .orders(order)
                .build();
    }

}
