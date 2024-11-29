package com.ecyce.karma.domain.user.dto.response;

import com.ecyce.karma.domain.address.entity.Address;
import com.ecyce.karma.domain.user.dto.request.UserInfoRequest;
import com.ecyce.karma.domain.user.entity.User;

public record UserInfo(
        Long userId ,
        String name,
        String nickname,
        String email ,
        String phoneNumber,
        String profileImageUrl,
        String postalCode ,
        String address1,
        String address2

) {

   /* 주소는 처음 저장된 걸 불러옴 */
    public static UserInfo from(User user , Address address) {

        return new UserInfo(
                user.getUserId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImage(),
                address.getPostalCode(),
                address.getAddress1(),
                address.getAddress2()
        );
    }

    /* 주소는 처음 저장된 걸 불러옴 */
    public static UserInfo of(User user) {

        return new UserInfo(
                user.getUserId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImage(),
                user.getAddress() != null ? user.getAddress().getPostalCode() : null,
                user.getAddress() != null ? user.getAddress().getAddress1() : null,
                user.getAddress() != null ? user.getAddress().getAddress2() : null
        );
    }


}
