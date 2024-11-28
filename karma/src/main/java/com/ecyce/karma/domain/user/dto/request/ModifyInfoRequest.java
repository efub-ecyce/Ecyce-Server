package com.ecyce.karma.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyInfoRequest{

    JsonNullable<String> name;
    JsonNullable<String> nickname;
    JsonNullable<String> bio;
    JsonNullable<String> phoneNumber;

    public ModifyInfoRequest(JsonNullable<String> name , JsonNullable<String> nickname , JsonNullable<String> bio , JsonNullable<String > phoneNumber){
        this.name = name;
        this.nickname = nickname;
        this.bio = bio;
        this.phoneNumber = phoneNumber;
    }
}
