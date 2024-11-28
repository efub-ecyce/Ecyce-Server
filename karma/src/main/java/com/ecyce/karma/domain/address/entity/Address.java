package com.ecyce.karma.domain.address.entity;

import com.ecyce.karma.domain.user.dto.request.ModifyAddressRequest;
import com.ecyce.karma.domain.user.dto.request.UserInfoRequest;
import com.ecyce.karma.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressId")
    private Long addressId;

    @Column(nullable = false)
    private String address1; // 도시 , 구

    @Column(nullable = false)
    private String address2; // 건물

    @Column(nullable = false)
    private String postalCode; // 우편번호

    @OneToOne
    @JoinColumn(name = "userId" ,updatable = false, nullable = false)
    private User user;

    @Builder
    public Address(User user , String address1 , String address2 , String postalCode){
        this.user = user;
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
    }

    /* 주소 형식 포맷팅 */
    @Override
    public String toString() {
        return String.format("[%d] %s %s", postalCode, address1, address2);
    }

    /* 초기 사용자인 경우 , 모든 주소를 update*/
    public static Address toEntity(User user , UserInfoRequest request){
        return Address.builder()
                .user(user)
                .postalCode(request.postalCode())
                .address1(request.address1())
                .address2(request.address2())
                .build();
    }

    /* 주소 중 원하는 부분만 update */
    public void updateAddress(ModifyAddressRequest request){
        if(request.getPostalCode()!= null && request.getPostalCode().isPresent()){
            this.postalCode = request.getPostalCode().get();
        }
        if(request.getAddress1()!= null && request.getAddress1().isPresent()){
            this.address1 = request.getAddress1().get();
        }
        if(request.getAddress2()!= null && request.getAddress2().isPresent()){
            this.address2 = request.getAddress2().get();
        }
    }

    public void setUser(User user1){
        this.user = user1;
    }
}
