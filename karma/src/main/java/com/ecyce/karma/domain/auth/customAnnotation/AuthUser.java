package com.ecyce.karma.domain.auth.customAnnotation;

import java.lang.annotation.*;

// 1. 커스텀 애노테이션 생성
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthUser {
}
