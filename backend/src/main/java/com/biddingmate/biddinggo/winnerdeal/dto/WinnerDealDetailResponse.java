package com.biddingmate.biddinggo.winnerdeal.dto;

import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "낙찰 거래 상세 조회 응답")
public class WinnerDealDetailResponse {
    @Schema(description = "낙찰 거래 ID", example = "101")
    private Long winnerDealId;

    @Schema(description = "거래 번호", example = "WD_20260409_A1B2C3")
    private String dealNumber;

    @Schema(description = "경매 ID", example = "201")
    private Long auctionId;

    @Schema(description = "상품 ID", example = "301")
    private Long itemId;

    @Schema(description = "조회자 역할", example = "BUYER")
    private String viewerRole;

    @Schema(description = "상품명", example = "롤렉스 시계")
    private String itemName;

    @Schema(description = "상품 이미지 URL", example = "https://cdn.example.com/item/1.jpg")
    private String itemImageUrl;

    @Schema(description = "화면 표시용 거래 상태", example = "SHIPPING")
    private WinnerDealStatus status;

    @Schema(description = "거래 금액", example = "1500000")
    private Long winnerPrice;

    @Schema(description = "판매자 닉네임", example = "seller1")
    private String sellerName;

    @Schema(description = "구매자 닉네임", example = "buyer1")
    private String winnerName;

    @Schema(description = "수령인", example = "홍길동")
    private String recipient;

    @Schema(description = "연락처", example = "010-1234-5678")
    private String tel;

    @Schema(description = "우편번호", example = "06109")
    private String zipcode;

    @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 152")
    private String address;

    @Schema(description = "상세 주소", example = "강남파이낸스센터 12층")
    private String detailAddress;

    @Schema(description = "택배사", example = "CJ Logistics")
    private String carrier;

    @Schema(description = "송장 번호", example = "1234567890")
    private String trackingNumber;

    @Schema(description = "배송지 등록 가능 여부", example = "true")
    private Boolean canRegisterShippingAddress;

    @Schema(description = "송장 등록 가능 여부", example = "false")
    private Boolean canRegisterTrackingNumber;

    @Schema(description = "구매 확정 가능 여부", example = "false")
    private Boolean canConfirmPurchase;

    @Schema(description = "리뷰 작성 가능 여부", example = "false")
    private Boolean canWriteReview;

    @Schema(description = "구매 확정일", example = "2026-03-05T18:30:00")
    private LocalDateTime confirmedAt;

    @Schema(description = "거래 일시", example = "2026-03-03T14:00:00")
    private LocalDateTime createdAt;
}
