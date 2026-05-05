package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "경매 목록 조회 요청 DTO")
public class AuctionListRequest extends BasePageRequest {
    @Schema(description = "정렬 기준", example = "CREATED_AT", allowableValues = {"CREATED_AT", "WISH_COUNT", "POPULARITY", "PRICE"}, nullable = true)
    private String sortBy;

    @Schema(description = "경매 상태", example = "ON_GOING", nullable = true)
    private String status;

    @Schema(description = "판매자 ID", example = "1", nullable = true)
    @Positive(message = "판매자 ID는 1 이상이어야 합니다.")
    private Long sellerId;

    @Schema(description = "카테고리 ID", example = "100", nullable = true)
    @Positive(message = "카테고리 ID는 1 이상이어야 합니다.")
    private Long categoryId;
}
