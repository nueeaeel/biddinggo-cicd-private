package com.biddingmate.biddinggo.address.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "배송지 조회 응답 DTO")
public class AddressListResponse {
    @Schema(description = "배송지 ID", example = "1")
    private Long id;

    @Schema(description = "우편번호", example = "06236")
    private String zipcode;

    @Schema(description = "기본 주소", example = "서울특별시 강남구 테헤란로 123")
    private String address;

    @Schema(description = "상세 주소", example = "101동 202호", nullable = true)
    private String detailAddress;

    @Schema(description = "기본 배송지 여부", example = "true")
    private boolean defaultYn;

    @Schema(description = "생성 일시", example = "2026-04-05T12:30:00", type = "string", format = "date-time")
    private LocalDateTime createdAt;
}
