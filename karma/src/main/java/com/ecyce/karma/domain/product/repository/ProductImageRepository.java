package com.ecyce.karma.domain.product.repository;

import com.ecyce.karma.domain.product.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage , Long> {

    @Query("select i.productImgUrl from ProductImage i where i.productImgId =:imgId")
    String findByImgId(@Param("imgId")Long imgId);

    @Query("select i from ProductImage i where i.productImgId =:imgId")
    ProductImage findByProductImgId(@Param("imgId")Long imgId);
}
