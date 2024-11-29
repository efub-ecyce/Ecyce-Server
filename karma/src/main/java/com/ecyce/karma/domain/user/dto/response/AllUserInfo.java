package com.ecyce.karma.domain.user.dto.response;

import com.ecyce.karma.domain.address.entity.Address;
import com.ecyce.karma.domain.user.entity.User;

public record AllUserInfo(

        Long userId ,
        String name,
        String nickname,
        String bio,
        String email ,
        String profileImageUrl,
        String phoneNumber,
        String postalCode ,
        String address1,
        String address2

) {

    public static AllUserInfo from(User user) {

        return new AllUserInfo(
                user.getUserId(),
                user.getName(),
                user.getNickname(),
                user.getBio(),
                user.getEmail(),
                user.getProfileImage(),
                user.getPhoneNumber(),
                user.getAddress() != null ? user.getAddress().getPostalCode() : null,
                user.getAddress() != null ? user.getAddress().getAddress1() : null,
                user.getAddress() != null ? user.getAddress().getAddress2() : null
        );
    }

}
