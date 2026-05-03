package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.common.request.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
/**
 * 유사도 기반 경매 검색 요청 DTO.
 * 검색어는 필수이고, 정렬 기준은 기존 경매 목록 규칙을 재사용한다.
 * 기본 정렬은 최신순(CREATED_AT DESC)이다.
 */
@Schema(description = "유사도 기반 경매 검색 요청 DTO")
public class AuctionSemanticSearchRequest extends BasePageRequest {
    public AuctionSemanticSearchRequest() {
        setOrder("DESC");
    }

    @NotBlank(message = "검색어는 필수입니다.")
    @Schema(description = "검색어", example = "나이키 조던 검정 하이탑")
    private String q;

    @Schema(description = "정렬 기준", example = "CREATED_AT", allowableValues = {"CREATED_AT", "WISH_COUNT", "POPULARITY", "PRICE"}, nullable = true)
    private String sortBy;
}
