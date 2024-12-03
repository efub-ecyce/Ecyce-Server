package com.ecyce.karma.domain.product.service;

import com.ecyce.karma.domain.bookmark.repository.BookmarkRepository;
import com.ecyce.karma.domain.product.dto.request.ModifyOptionRequest;
import com.ecyce.karma.domain.product.dto.request.ModifyProductRequest;
import com.ecyce.karma.domain.product.dto.request.OptionRequest;
import com.ecyce.karma.domain.product.dto.response.OptionResponse;
import com.ecyce.karma.domain.product.dto.response.ProductDetailResponse;
import com.ecyce.karma.domain.product.dto.request.ProductRequest;
import com.ecyce.karma.domain.product.dto.response.ProductImgsResponse;
import com.ecyce.karma.domain.product.dto.response.ProductSimpleResponse;
import com.ecyce.karma.domain.product.entity.Product;
import com.ecyce.karma.domain.product.entity.ProductImage;
import com.ecyce.karma.domain.product.entity.ProductOption;
import com.ecyce.karma.domain.product.repository.ProductImageRepository;
import com.ecyce.karma.domain.product.repository.ProductOptionRepository;
import com.ecyce.karma.domain.product.repository.ProductRepository;
import com.ecyce.karma.domain.s3.S3Uploader;
import com.ecyce.karma.domain.user.entity.User;
import com.ecyce.karma.global.exception.CustomException;
import com.ecyce.karma.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final S3Uploader s3Uploader;
    private final ProductImageRepository productImageRepository;

    /* 제품 상세 조회 */
    public ProductDetailResponse getProductDetail(User user , Long productId) {

        Product product = findProduct(productId);

        Boolean isMarked = bookmarkRepository.isBookmarked(productId , user.getUserId());


       return ProductDetailResponse.from(product , isMarked);
    }

    /* 제품 등록 */
    public ProductDetailResponse registerProduct(User user, List<MultipartFile> fileList, ProductRequest dto) {
        Product product = ProductRequest.toEntity(user , dto);
        // 소재 이미지 저장
        String materialExampleUrl = s3Uploader.saveFile(fileList.get(0));
        product.updateMaterialExample(materialExampleUrl);
        productRepository.save(product);

        // 상품 이미지 저장
         List<String> productImgs = new ArrayList<>();
          productImgs = s3Uploader.saveFiles(fileList.subList(1, fileList.size()));

        productImgs.forEach(productImgUrl -> {
            ProductImage productImage = ProductImage.builder()
                    .productImgUrl(productImgUrl)
                    .product(product)
                    .build();
            productImageRepository.save(productImage);
        });

        // 옵션이 있다면
        if (dto.getOptions() != null) {
            for (OptionRequest optionDto : dto.getOptions()) {
                ProductOption productOption = optionDto.toEntity(product , optionDto);
                productOptionRepository.save(productOption);
            }
        }

        List<ProductOption> productOptions = productOptionRepository.findByProductId(product.getProductId());
        List<OptionResponse> optionResponseDtos = productOptions.stream()
                .map(productOption -> OptionResponse.from(productOption))
                .collect(Collectors.toList());

        return ProductDetailResponse.of(product , optionResponseDtos);
    }

    /*상품 리스트 조회 */
    public List<ProductSimpleResponse> getProductList(User user) {
        List<Product> productList = productRepository.findAll();
        return getProductSimpleResponses(user, productList);
    }

    /* 특정 작가의 작품 리스트 반환 */
    public List<ProductSimpleResponse> getProductListOfArtist(Long artistId ,User user) {
        List<Product> productList = productRepository.findByUserId(artistId);
        return getProductSimpleResponses(user, productList);
    }

    /* 요청을 한 사용자의 북마크 여부를 표시하기 위한 메서드 */
    private List<ProductSimpleResponse> getProductSimpleResponses(User user, List<Product> productList) {
        if(user == null){ // 로그인하지 않은 사용자
            List<ProductSimpleResponse> productListwithoutBookmark = productList.stream()
                    .map(product -> ProductSimpleResponse.from(product , false))
                    .collect(Collectors.toList());
            return productListwithoutBookmark;
        }
        else { //로그인한 사용자
            List<ProductSimpleResponse> productListwithBookmark = productList.stream()
                    .map(product -> {
                        // 북마크 여부 확인
                        Boolean isMarked = bookmarkRepository.isBookmarked(product.getProductId(), user.getUserId());
                        return ProductSimpleResponse.from(product, isMarked);
                    })
                    .collect(Collectors.toList());
            return productListwithBookmark;
        }
    }


    /* 상품 정보만 수정*/
    public ProductDetailResponse modifyProduct(User user, Long productId , ModifyProductRequest dto) {
        Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        product.updateInfo(dto);

        productRepository.save(product);

        List<ProductOption> productOptions = productOptionRepository.findByProductId(product.getProductId());
        List<OptionResponse> optionResponseDtos = productOptions.stream()
                .map(productOption -> OptionResponse.from(productOption))
                .collect(Collectors.toList());

        return ProductDetailResponse.of(product, optionResponseDtos);

    }

    /* 상품 정보 삭제 */
    public void deleteProduct(User user, Long productId) {
        Product product = findProduct(productId);

        // 삭제하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        productRepository.delete(product);
    }

    /* 상품 옵션 정보 수정 */
    public OptionResponse modifyOptions(User user, Long productId, Long optionId, ModifyOptionRequest dto) {
        ProductOption productOption = productOptionRepository.findByProductIdAndOptionId(productId , optionId);
        Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        productOption.updateOption(dto);

        productOptionRepository.save(productOption);

        OptionResponse optionResponse = OptionResponse.from(productOption);
        return optionResponse;
    }

    /* DB에서 product 찾기 */
    private Product findProduct(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        return  product;
    }

    /* 상품 검색 */
    public ResponseEntity<?> Search(User user, String searchWord) {
        List<Product> productList = productRepository.findByProductName(searchWord);

        List<ProductSimpleResponse> productSimpleResponseList = getProductSimpleResponses(user, productList);

        return ResponseEntity.status(HttpStatus.OK).body(productSimpleResponseList);
    }

    /* 카테고리별 조회*/
    public ResponseEntity<?> categorySort(User user, Long category) {

        if(category.equals(1L)){ // 후기순
            List<Product> sortedProducts = productRepository.findAllOrderByRatingDesc(); // 정렬된 결과 가져오기
            // 로그인 여부에 따른 북마크 상태 포함
            List<ProductSimpleResponse> productSimpleResponses = getProductSimpleResponses(user, sortedProducts);
            return ResponseEntity.ok(productSimpleResponses);
        }
        else if(category.equals(2L)){ // 북마크 개수 순
            List<Product> sortedProducts = productRepository.findAllOrderByBookmarkCountDesc();
            // 로그인 여부에 따른 북마크 상태 포함
            List<ProductSimpleResponse> productSimpleResponses = getProductSimpleResponses(user, sortedProducts);
            return ResponseEntity.ok(productSimpleResponses);
        }
        else if(category.equals(3L)){ // 최근순
           List<Product> sortedProducts = productRepository.findAllOrderByCreatedAtDesc();
           List<ProductSimpleResponse> productSimpleResponse = getProductSimpleResponses(user , sortedProducts);
           return ResponseEntity.ok(productSimpleResponse);
        }
        else throw  new CustomException(ErrorCode.INVALID_REQUEST);

    }

    /* 옵션 삭제 */
    public void deleteOption(User user , Long productId , Long optionId) {
        ProductOption productOption = productOptionRepository.findByProductIdAndOptionId(productId , optionId);
        Product product = findProduct(productId);
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        productOptionRepository.delete(productOption);
    }

    /* 옵션 추가 */
    public ResponseEntity<OptionResponse> addOption(User user, Long productId, OptionRequest request) {
          Product product = findProduct(productId);
          if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
          }
          ProductOption productOption = OptionRequest.toEntity(product ,request);
          productOptionRepository.save(productOption);
          OptionResponse response = OptionResponse.from(productOption);
          return  ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* 소재 예시 사진 수정 */
    public ResponseEntity<ProductDetailResponse> modifyMaterialEX(User user, Long productId , MultipartFile file) {
       Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

       String materialEXUrl = productRepository.findMaterialEx(productId);

        if(materialEXUrl != null && !materialEXUrl.isBlank()){
            s3Uploader.deleteFile(materialEXUrl);
        }
       String newUrl = s3Uploader.saveFile(file);

       product.updateMaterialExample(newUrl);

       List<ProductOption> productOptions = productOptionRepository.findByProductId(productId);
       List<OptionResponse> optionResponses = productOptions.stream()
               .map(productOption -> OptionResponse.from(productOption))
               .collect(Collectors.toList());

       ProductDetailResponse response = ProductDetailResponse.of(product , optionResponses);
       return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /* 소재 사진 삭제 */
    public ResponseEntity<String> deleteMaterialEx(User user, Long productId) {
        Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }
        String materialEXUrl = productRepository.findMaterialEx(productId);
        if(materialEXUrl == null){
            throw  new CustomException(ErrorCode.MATERIAL_EX_NOT_FOUND);
        }
        s3Uploader.deleteFile(materialEXUrl);

        product.updateMaterialExample(null); // 삭제

        return ResponseEntity.status(HttpStatus.OK)
                .body("삭제되었습니다.");
    }

    /* 요청 보내는 사용자와 판매자와 일치하는지 확인  */
    private boolean isRequestEqualsSeller(User user , Product product){
        if(!user.getUserId().equals(product.getUser().getUserId())){
          return false;
        }
        else return true;
    }

    /* 상품 이미지 추가 */
    public ResponseEntity<ProductImgsResponse> addProductImg(User user, Long productId, MultipartFile file) {
        Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        String url = s3Uploader.saveFile(file);

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .productImgUrl(url)
                .build();

        productImageRepository.save(productImage);

        ProductImgsResponse productImgsResponse = ProductImgsResponse.from(productImage);

        return ResponseEntity.status(HttpStatus.OK).body(productImgsResponse);

    }

    /* 상품 이미지 삭제 */
    public ResponseEntity<String> deleteProductImg(User user, Long productId, Long imgId) {
        Product product = findProduct(productId);
        // 수정하려는 사용자와 상품의 판매자가 일치하지 않을 때
        if(!isRequestEqualsSeller(user , product)){
            throw new CustomException(ErrorCode.INVALID_ACCESS);
        }

        String url = productImageRepository.findByImgId(imgId);

        s3Uploader.deleteFile(url);

        ProductImage productImage = productImageRepository.findByProductImgId(imgId);
        productImageRepository.delete(productImage);

        return  ResponseEntity.status(HttpStatus.OK).body("삭제되었습니다.");
    }


}
