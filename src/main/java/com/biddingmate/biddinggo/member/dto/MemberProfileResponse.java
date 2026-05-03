package com.biddingmate.biddinggo.member.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MemberProfileResponse {

    private String imageUrl;
    private String name;
    private String nickname;
    private String email;

    private String zipcode;
    private String address;
    private String detailAddress;

    private String bankCode;
    private String bankAccount;

    private LocalDateTime createdAt;
    private long reviewCount;
    private Double ratingAvg;

    private String grade;
}
