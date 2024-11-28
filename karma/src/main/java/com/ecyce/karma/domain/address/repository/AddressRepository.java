package com.ecyce.karma.domain.address.repository;

import com.ecyce.karma.domain.address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address , Long> {

    @Query("select a from Address  a where a.user.userId =:userId")
    Optional<Address> findByUserId(@Param("userId")Long userId);
}
