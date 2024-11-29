package com.ecyce.karma.domain.user.entity;

import com.ecyce.karma.domain.address.entity.Address;
import com.ecyce.karma.domain.bookmark.entity.Bookmark;
import com.ecyce.karma.domain.notice.entity.Notice;
import com.ecyce.karma.domain.order.entity.Orders;
import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.user.dto.request.ModifyInfoRequest;
import com.ecyce.karma.domain.user.dto.request.UserInfoRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long userId;

    @Column(length = 20)
    private String name;

    @Column(length = 20 , unique = true) //닉네임 중복 불가
    private String nickname;

    @Column(updatable = false)
    private String email;

    @Column(length = 1024)
    private String profileImage;

    @Column(length = 15 , unique = true)
    private String phoneNumber;

    @Column(length = 200)
    private String bio;


    @Column
    private String kakaoAccessToken;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<Bookmark> bookmarks = new ArrayList<>();

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL, orphanRemoval = true)
    List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "sellerUser", cascade = CascadeType.ALL, orphanRemoval = true)
     List<Orders> userAsSeller = new ArrayList<>();

    @OneToMany(mappedBy = "buyerUser", cascade = CascadeType.ALL, orphanRemoval = true)
     List<Orders> userAsBuyer = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    Address address;

    @Builder
    public  User(String nickname , String email , String profileImage , String phoneNumber , String bio ,String kakaoAccessToken, Address address){
        this.nickname = nickname;
        this.email  = email;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.kakaoAccessToken = kakaoAccessToken;
        this.address = address;
    }

    /* 액세스 토큰 업데이트 */
    public void updateKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    /* 이미지 url 업데이트 */
    public void updateProfileImage(String url){
        this.profileImage = url;
    }

    /* 새로운 회원인 경우 정보 저장*/
    public void updateNewUserInfo(User user , UserInfoRequest userInfoRequest){
        this.name = userInfoRequest.name();
        this.nickname = userInfoRequest.nickname();
        this.phoneNumber = userInfoRequest.phoneNumber();
        Address newAddress = Address.builder()
                .postalCode(userInfoRequest.postalCode())
                .address1(userInfoRequest.address1())
                .address2(userInfoRequest.address2())
                .build();
        newAddress.setUser(user);
    }

    /* 회원 정보 수정 메서드 */
    public void updateUserInfo(ModifyInfoRequest request) {
        if (request.getName() != null && request.getName().isPresent()) {
            log.info("name null인가 {}" , request.getName());
            this.name = request.getName().get();
        }
        if (request.getNickname() != null && request.getNickname().isPresent()) {
            this.nickname = request.getNickname().get();
        }
        if (request.getBio() != null &&request.getBio().isPresent()) {
            this.bio = request.getBio().get();
        }
        if (request.getPhoneNumber() != null &&request.getPhoneNumber().isPresent()) {
            this.phoneNumber = request.getPhoneNumber().get();
        }

    }


    public void setName(String name) {
        this.name = name;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

