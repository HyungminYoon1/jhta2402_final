package com.user.IntArear.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Platform {
    EMAIL("EMAIL"),
    GOOGLE("GOOGLE"),
    GIT("GIT"),
    NAVER("NAVER"),
    KAKAO("KAKAO"),;

    private final String platform;
}
