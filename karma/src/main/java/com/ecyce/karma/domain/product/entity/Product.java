package com.ecyce.karma.domain.product.entity;

import com.ecyce.karma.domain.bookmark.entity.Bookmark;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.product.dto.request.ModifyProductRequest;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productId")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "userId" ,updatable = false, nullable = false)
    private User user;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int duration;

    @Column
    private double rating;  // 점수는 아무도 리뷰를 쓰지 않은 경우 , 없을 수 있음

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductState productState;

    @Column
    private Integer deliveryFee; // 배송비

    @Column
    private String materialInfo; // 소재의 정보

//    @Column
//    private String materialExample; // 소재의 정보

    @Column
    private String buyerNotice; // 구매자 안내사항


    @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Orders> orders  = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Builder
    public Product(User user , String productName , int price , String content , int duration , double rating , ProductState productState,
    Integer deliveryFee , String materialInfo, String buyerNotice){
        this.user = user;
        this.productName = productName;
        this.price = price;
        this.content = content;
        this.duration = duration;
        this.rating = rating;
        this.productState = productState;
        this.deliveryFee = deliveryFee;
        this.materialInfo = materialInfo;
        this.buyerNotice = buyerNotice;
    }

    /* 상품 정보 update */
    public void updateInfo(ModifyProductRequest dto) {
        if(dto.getProductName()!= null && dto.getProductName().isPresent()){
           this.productName = dto.getProductName().get();
        }
        if(dto.getPrice()!= null && dto.getPrice().isPresent()){
            this.price= dto.getPrice().get();
        }
        if(dto.getContent()!= null && dto.getContent().isPresent()){
            this.content = dto.getContent().get();
        }
        if(dto.getDuration()!= null && dto.getDuration().isPresent()){
            this.duration = dto.getDuration().get();
        }
        if(dto.getRating()!= null && dto.getRating().isPresent()){
            this.rating = dto.getRating().get();
        }
        if(dto.getProductState()!= null && dto.getProductState().isPresent()){
            this.productState = dto.getProductState().get();
        }
        if (dto.getDeliveryFee()!= null && dto.getDeliveryFee().isPresent()) {
            this.deliveryFee= dto.getDeliveryFee().get();
        }
        if(dto.getMaterialInfo()!= null && dto.getMaterialInfo().isPresent()){
            this.materialInfo = dto.getMaterialInfo().get();
        }
        if(dto.getBuyerNotice()!= null && dto.getBuyerNotice().isPresent()){
            this.buyerNotice = dto.getBuyerNotice().get();
        }
    }
    public void updateRating(double rating){
        this.rating = rating;
    }
}
