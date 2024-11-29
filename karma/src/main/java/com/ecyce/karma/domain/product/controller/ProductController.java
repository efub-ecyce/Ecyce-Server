package com.ecyce.karma.domain.product.controller;

import com.ecyce.karma.domain.auth.customAnnotation.AuthUser;
import com.ecyce.karma.domain.bookmark.service.BookmarkService;
import com.ecyce.karma.domain.product.dto.request.ModifyOptionRequest;
import com.ecyce.karma.domain.product.dto.request.ModifyProductRequest;
import com.ecyce.karma.domain.product.dto.request.OptionRequest;
import com.ecyce.karma.domain.product.dto.response.OptionResponse;
import com.ecyce.karma.domain.product.dto.response.ProductDetailResponse;
import com.ecyce.karma.domain.product.dto.request.ProductRequest;
import com.ecyce.karma.domain.product.dto.response.ProductSimpleResponse;
import com.ecyce.karma.domain.product.service.ProductService;
import com.ecyce.karma.domain.review.dto.ReviewDetailDto;
import com.ecyce.karma.domain.review.service.ReviewService;
import com.ecyce.karma.domain.s3.S3Uploader;
import com.ecyce.karma.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final BookmarkService bookmarkService;
    private final ReviewService reviewService;

    /* 상품 등록  , 현재 사진 업로드하는 코드는 포함되어 있지 않음 */
    @PostMapping
    public ResponseEntity<ProductDetailResponse> registerProduct(@AuthUser User user ,
                                                                 @RequestPart(value = "productImages") List<MultipartFile> fileList,
                                                                 @RequestPart(value ="request") ProductRequest dto){
         ProductDetailResponse response = productService.registerProduct(user  , fileList ,dto);
         return ResponseEntity.status(HttpStatus.CREATED)
                 .body(response);
    }

   /* 단일 상품 조회 (상세)*/
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(Optional<User> userOpt , @PathVariable("productId") Long productId){
        User user = userOpt.orElse(null); // user가 없으면 null로 설정
        ProductDetailResponse dto = productService.getProductDetail(user ,productId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(dto);
    }

    /* 상품 리스트 조회*/
    @GetMapping
    public ResponseEntity<List<ProductSimpleResponse>> getProductList(Optional<User> userOpt){
        User user = userOpt.orElse(null); // user가 없으면 null로 설정
        List<ProductSimpleResponse> dtos = productService.getProductList(user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(dtos);
    }

    /* 상품 수정 */
    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> modifyProduct(@AuthUser User user ,@PathVariable("productId") Long productId , @RequestBody ModifyProductRequest dto){
        ProductDetailResponse response = productService.modifyProduct(user , productId , dto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    /* 북마크 등록 */
    @PostMapping("/{productId}/bookmark")
    public ResponseEntity<String> addBookmark(@AuthUser User user, @PathVariable("productId") Long productId) {
        bookmarkService.create(productId, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body("북마크가 등록되었습니다.");
    }

    /* 북마크 삭제 */
    @DeleteMapping("/{productId}/bookmark")
    public ResponseEntity<String> deleteBookmark(@AuthUser User user, @PathVariable("productId") Long productId) {
        bookmarkService.delete(productId, user.getUserId());
        return ResponseEntity.status(HttpStatus.OK).body("북마크가 삭제되었습니다.");
    }


    /* 상품 Option 정보 수정 */
    @PatchMapping("/{productId}/option/{optionId}")
    public ResponseEntity<OptionResponse> modifyOptions(@AuthUser User user , @PathVariable("productId") Long productId , @PathVariable("optionId") Long optionId , @RequestBody ModifyOptionRequest dto){
       OptionResponse optionResponse = productService.modifyOptions(user , productId , optionId , dto);
       return ResponseEntity.status(HttpStatus.OK).body(optionResponse);
    }

    /* 상품 삭제 */
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@AuthUser User user , @PathVariable("productId") Long productId){
        productService.deleteProduct(user , productId);
        return ResponseEntity.status(HttpStatus.OK).body("상품 정보가 삭제되었습니다.");
    }

    /* 상품 후기 목록 조회 */
    @GetMapping("/{productId}/reviews")
    public ResponseEntity<List<ReviewDetailDto>> getProductReviews(@PathVariable("productId") Long productId,
                                                                   @RequestParam(name = "sort", defaultValue = "default") String sort) {
        List<ReviewDetailDto> reviews = reviewService.getProductReviews(productId, sort);
        return ResponseEntity.ok(reviews);
    }


    /* 상품 검색 */
    @GetMapping("/search")
    public ResponseEntity<?> SearchAllByWord(@AuthUser User user ,@RequestParam(name ="word") String searchWord){
        return productService.Search(user , searchWord);
    }

    /* 카테고리별 조회 */
    @GetMapping("/category")
    public ResponseEntity<?> categorySort(@AuthUser User user , @RequestParam(name ="code") Long category){
        return productService.categorySort(user , category);
    }

    /* 옵션 삭제 */
    @DeleteMapping("/{productId}/option/{optionId}")
    public String deleteOption(@AuthUser User user ,@PathVariable("productId") Long productId , @PathVariable("optionId") Long optionId){
        productService.deleteOption(user , productId , optionId);
        return "삭제되었습니다.";
    }

    /* 옵션 추가 */
    @PostMapping("/{productId}/option")
    public ResponseEntity<OptionResponse> addOption(@AuthUser User user , @PathVariable("productId") Long productId , @RequestBody OptionRequest request){
        return productService.addOption(user , productId , request);
    }


}
