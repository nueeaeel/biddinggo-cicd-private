package com.biddingmate.biddinggo.auction.controller;

import com.biddingmate.biddinggo.auction.dto.AuctionDetailResponse;
import com.biddingmate.biddinggo.auction.dto.AuctionListRequest;
import com.biddingmate.biddinggo.auction.dto.AuctionListResponse;
import com.biddingmate.biddinggo.auction.dto.AuctionSemanticSearchRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionFromInspectionItemRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionRequest;
import com.biddingmate.biddinggo.auction.dto.CreateAuctionResponse;
import com.biddingmate.biddinggo.auction.dto.UpdateAuctionRequest;
import com.biddingmate.biddinggo.auction.service.AuctionApplicationService;
import com.biddingmate.biddinggo.auction.service.AuctionQueryService;
import com.biddingmate.biddinggo.auction.service.AuctionService;
import io.swagger.v3.oas.annotations.Operation;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
@Tag(name = "Auction", description = "경매 등록 API")
public class AuctionController {
    private final AuctionApplicationService auctionApplicationService;
    private final AuctionQueryService auctionQueryService;
    private final AuctionService auctionService;

    @GetMapping("")
    @Operation(summary = "경매 목록 조회", description = "조건에 맞는 경매 목록을 페이징 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AuctionListResponse>>> getAuctionList(
            @Valid AuctionListRequest request) {

        PageResponse<AuctionListResponse> result = auctionQueryService.getAuctionList(request);

        return ApiResponse.of(HttpStatus.OK, null, "경매 목록 조회 완료", result);
    }

    @GetMapping("/search/semantic")
    @Operation(summary = "유사도 기반 경매 검색", description = "검색어를 기반으로 유사한 진행 중 경매를 조회하며, 정렬은 기존 경매 목록 기준을 따릅니다.")
    public ResponseEntity<ApiResponse<PageResponse<AuctionListResponse>>> searchAuctionsBySemantic(
            @Valid AuctionSemanticSearchRequest request) {

        // semantic search는 후보 추출만 유사도 기반으로 수행하고, 최종 정렬은 기존 목록 규칙을 따른다.
        PageResponse<AuctionListResponse> result = auctionQueryService.searchAuctionsBySemantic(request);

        return ApiResponse.of(HttpStatus.OK, null, "유사도 기반 경매 검색 완료", result);
    }

    @GetMapping("/{auctionId}")
    @Operation(summary = "경매 상세 조회", description = "경매 기본 정보, 상품 정보, 카테고리, 이미지 목록과 예측가 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<AuctionDetailResponse>> getAuctionDetail(
            @PathVariable Long auctionId) {

        AuctionDetailResponse result = auctionQueryService.getAuctionDetail(auctionId);

        return ApiResponse.of(HttpStatus.OK, null, "경매 상세 조회 완료", result);
    }

    @PostMapping("")
    @Operation(summary = "경매 등록", description = "경매 상품과 경매 정보를 함께 등록합니다.")
    public ResponseEntity<ApiResponse<CreateAuctionResponse>> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal Member member) {

        Long auctionId = auctionApplicationService.createAuction(request, member.getId());

        CreateAuctionResponse result = CreateAuctionResponse.builder()
                .auctionId(auctionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "경매 등록 완료", result);
    }

    @PostMapping("/inspection-items")
    @Operation(summary = "검수 완료 상품 경매 등록", description = "검수 완료된 기존 상품을 기준으로 경매를 등록합니다.")
    public ResponseEntity<ApiResponse<CreateAuctionResponse>> createAuctionFromInspectionItem(
            @Valid @RequestBody CreateAuctionFromInspectionItemRequest request,
            @AuthenticationPrincipal Member member) {

        Long auctionId = auctionApplicationService.createAuctionFromInspectionItem(request, member.getId());

        CreateAuctionResponse result = CreateAuctionResponse.builder()
                .auctionId(auctionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "검수 완료 상품 경매 등록 완료", result);
    }

    @PatchMapping("/{auctionId}")
    @Operation(summary = "경매 정보 수정", description = "경매 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<CreateAuctionResponse>> updateAuction(
            @PathVariable Long auctionId,
            @Valid @RequestBody UpdateAuctionRequest request,
            @AuthenticationPrincipal Member member) {

        auctionService.updateAuction(auctionId, request, member.getId());

        CreateAuctionResponse result = CreateAuctionResponse.builder()
                .auctionId(auctionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "경매 정보 수정 완료", result);
    }

    @PatchMapping("/{auctionId}/cancel")
    @Operation(summary = "경매 취소", description = "경매를 취소 처리합니다.")
    public ResponseEntity<ApiResponse<CreateAuctionResponse>> cancelAuction(
            @PathVariable Long auctionId,
            @AuthenticationPrincipal Member member) {

        auctionService.cancelAuction(auctionId, member.getId());

        CreateAuctionResponse result = CreateAuctionResponse.builder()
                .auctionId(auctionId)
                .build();

        return ApiResponse.of(HttpStatus.OK, null, "경매 취소 완료", result);
    }
    @PostMapping("/{auctionId}/buy-now")
    @Operation(summary = "즉시 구매", description = "설정된 즉시구매가로 경매를 즉시 종료하고 거래를 확정합니다.")
    public ResponseEntity<ApiResponse<Void>> buyNowAuction(@Parameter(description = "경매 ID", example = "1") @PathVariable Long auctionId,
                                                           @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        auctionService.buyNowAuction(auctionId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "즉시 구매 완료", null);
    }
}
