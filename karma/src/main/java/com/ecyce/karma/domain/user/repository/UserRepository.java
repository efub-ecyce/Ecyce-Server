package com.ecyce.karma.domain.user.repository;

import com.ecyce.karma.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    User findByUserId(Long userId);

    @Query("select u.profileImage from User  u where u.userId =:userId")
    String findProfileImageUrl(@Param(("userId")) Long userId);
}
