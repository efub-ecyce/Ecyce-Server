package com.ecyce.karma.domain.product.repository;

import com.ecyce.karma.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product , Long> {

    /* 사용자 id로 제품 리스트 반환*/
    @Query("select p from Product  p where p.user.userId =:userId")
    List<Product> findByUserId(@Param("userId") Long userId);

    /* product name으로 조회 */
    @Query("select p from Product  p where p.productName LIKE %:searchWord%")
    List<Product> findByProductName(@Param("searchWord") String word);

    /* 모든 제품을 rating 기준으로 내림차순 정렬 (NULLS LAST 포함)*/
    @Query("SELECT p FROM Product p ORDER BY p.rating DESC NULLS LAST")
    List<Product> findAllOrderByRatingDesc();



    /* 모든 제품을 북마크 순으로 정렬 */
    @Query("SELECT p " +
            "FROM Product p " +
            "LEFT JOIN p.bookmarkList b " +
            "GROUP BY p " +
            "ORDER BY COUNT(b) DESC")
    List<Product> findAllOrderByBookmarkCountDesc();


    /* 최신순으로 정렬 */
    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    List<Product> findAllOrderByCreatedAtDesc();

    /* 소재 예시 사진 url 찾기 */
    @Query("SELECT p.materialExample from Product p where p.productId =:productId")
    String findMaterialEx(@Param("productId") Long productId);

}
