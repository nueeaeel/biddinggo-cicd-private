package com.biddingmate.biddinggo.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSellerProfileResponse {

    // 기본 프로필 정보
    private String nickname;
    private String imageUrl;
    private String grade;
    private LocalDateTime createdAt;

    // 판매 통계 정보
    private Double rating;
    private Integer reviewCount;
    private Long totalSales;
    private Integer cancelCount;
    private Integer responseRate;

}