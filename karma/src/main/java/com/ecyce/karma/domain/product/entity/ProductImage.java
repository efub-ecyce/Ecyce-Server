package com.ecyce.karma.domain.product.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productImageId")
    private Long productImgId;

    @Column(nullable = false)
    private String productImgUrl;

    @ManyToOne
    @JoinColumn(name = "productId" ,updatable = false, nullable = false)
    private Product product;

    @Builder
    public ProductImage(String productImgUrl , Product product){
        this.productImgUrl = productImgUrl;
        this.product = product;
    }

}
