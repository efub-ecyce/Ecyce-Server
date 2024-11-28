package com.ecyce.karma.domain.user.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.bookmark.dto.BookmarkDto;
import com.ecyce.karma.domain.bookmark.service.BookmarkService;
import com.ecyce.karma.domain.product.dto.response.ProductSimpleResponse;
import com.ecyce.karma.domain.product.service.ProductService;
import com.ecyce.karma.domain.user.dto.request.ModifyAddressRequest;
import com.ecyce.karma.domain.user.dto.request.ModifyInfoRequest;
import com.ecyce.karma.domain.review.dto.ReviewResponseDto;
import com.ecyce.karma.domain.review.service.ReviewService;
import com.ecyce.karma.domain.user.dto.request.UserInfoRequest;
import com.ecyce.karma.domain.user.dto.response.AllUserInfo;
import com.ecyce.karma.domain.user.dto.response.ArtistInfoResponse;
import com.ecyce.karma.domain.user.dto.response.UserInfo;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final BookmarkService bookmarkService;
    private final UserService userService;
    private final ProductService productService;

    /* 회원별 북마크 상품 목록 조회 */
    @GetMapping("/users/bookmarks")
    public ResponseEntity<List<BookmarkDto>> getBookmarks(@AuthUser User user) {
        List<BookmarkDto> bookmarks = bookmarkService.getUserBookmarks(user.getUserId());
        return ResponseEntity.ok(bookmarks);
    }

    /* 작가 정보 조회 */
    @GetMapping("/artist/{userId}")
    public ResponseEntity<ArtistInfoResponse> getArtistInfo(@PathVariable("userId") Long userId){
        ArtistInfoResponse response = userService.getArtistInfo(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /*작가의 작품 리스트 반환 */
    @GetMapping("/artist/{userId}/product")
    public ResponseEntity<List<ProductSimpleResponse>> getProductList(@PathVariable("userId")Long artistId, Optional<User> userOpt){
        User user = userOpt.orElse(null); // user가 없으면 null로 설정
        List<ProductSimpleResponse> dtos = productService.getProductListOfArtist(artistId ,user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    /* 회원 조회 */
    @GetMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(@AuthUser User user){
        UserInfo userInfo = userService.getUserInfo(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userInfo);
    }

    /* 처음 가입한 사용자인 경우 , 개인 정보 저장*/
    @PostMapping("/user")
    public ResponseEntity<UserInfo> saveNewUserInfo(@AuthUser User user , @RequestBody UserInfoRequest request){
        UserInfo userInfo = userService.saveNewUser(user,request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(userInfo);
    }

    /* 사용자 정보 수정 */
    @PatchMapping("/user")
    public ResponseEntity<AllUserInfo> modifyUserInfo(@AuthUser User user , @RequestBody ModifyInfoRequest request){
        AllUserInfo allUserInfo = userService.modifyUserInfo(user , request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(allUserInfo);
    }

    /* 사용자 주소 변경 */
    @PatchMapping("/user/address")
    public ResponseEntity<UserInfo> modifyUserAddress(@AuthUser User user , @RequestBody ModifyAddressRequest request){
        UserInfo userInfo = userService.modifyAddress(user , request);
        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }




}
