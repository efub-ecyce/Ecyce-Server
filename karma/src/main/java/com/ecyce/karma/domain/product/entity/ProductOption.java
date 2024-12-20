package com.ecyce.karma.domain.product.entity;

import com.ecyce.karma.domain.product.dto.request.ModifyOptionRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productOptionId")
    private Long optionId;

    @Column(nullable = false)
    private String optionName;

    @Column(nullable = false)
    private Integer optionPrice; // 옵션 추가 금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Builder
    public ProductOption(String optionName, Product product, int optionPrice) {
        this.optionName = optionName;
        this.optionPrice = optionPrice;
        this.product = product;
    }

    /* 상품 옵션 수정 */
    public void updateOption(ModifyOptionRequest dto){
       if(dto.getOptionName()!= null && dto.getOptionName().isPresent()){
           this.optionName = dto.getOptionName().get();
       }
       if(dto.getOptionPrice() != null && dto.getOptionPrice().isPresent()){
           this.optionPrice = dto.getOptionPrice().get();
       }

    }


}
