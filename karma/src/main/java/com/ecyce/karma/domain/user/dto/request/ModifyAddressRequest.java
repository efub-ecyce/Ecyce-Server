package com.ecyce.karma.domain.user.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyAddressRequest{
    JsonNullable<String> postalCode;
    JsonNullable<String> address1;
    JsonNullable<String> address2;

    public  ModifyAddressRequest(JsonNullable<String> postalCode , JsonNullable<String> address1 , JsonNullable<String> address2){
        this.postalCode = postalCode;
        this.address1 = address1;
        this.address2 = address2;
    }
}
