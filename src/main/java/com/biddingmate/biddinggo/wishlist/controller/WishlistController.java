package com.biddingmate.biddinggo.wishlist.controller;

import com.biddingmate.biddinggo.auction.dto.AuctionListResponse;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.ApiResponse;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.member.model.Member;
import com.biddingmate.biddinggo.wishlist.dto.CreateWishlistRequest;
import com.biddingmate.biddinggo.wishlist.dto.CreateWishlistResponse;
import com.biddingmate.biddinggo.wishlist.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "관심 경매 API")
public class WishlistController {
    private final WishlistService wishlistService;

    @PostMapping("")
    @Operation(summary = "관심 경매 등록", description = "관심 경매를 등록합니다.")
    public ResponseEntity<ApiResponse<CreateWishlistResponse>> createWishlist(
            @RequestBody CreateWishlistRequest request,
            @AuthenticationPrincipal Member member
    ) {

        CreateWishlistResponse result = wishlistService.createWishlist(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "관심 경매 등록 성공", result);
    }

    @GetMapping("")
    @Operation(summary = "내 관심 경매 조회", description = "사용자의 관심 경매를 조회합니다.")
    public ResponseEntity<ApiResponse<PageResponse<AuctionListResponse>>> getWishlist(
            BasePageRequest request,
            @RequestParam(required = false, defaultValue = "CREATED_AT") String sortBy,
            @AuthenticationPrincipal Member member
    ) {
        PageResponse<AuctionListResponse> result = wishlistService.getWishlistAuctionsByMemberId(request, sortBy, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "관심 경매 조회 성공", result);
    }

    @GetMapping("/status")
    @Operation(summary = "관심 경매 여부 조회", description = "특정 경매가 내 관심 경매인지 조회합니다.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWishlistStatus(
            @RequestParam Long auctionId,
            @AuthenticationPrincipal Member member
    ) {
        boolean wished = wishlistService.existsWishlist(auctionId, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "관심 경매 여부 조회 성공", Map.of(
                "auctionId", auctionId,
                "wished", wished
        ));
    }

    @DeleteMapping("")
    @Operation(summary = "관심 경매 삭제", description = "관심 경매를 삭제합니다.")
    public ResponseEntity<ApiResponse<Integer>> deleteWishlist(
            @RequestBody CreateWishlistRequest request,
            @AuthenticationPrincipal Member member
    ) {

        int result = wishlistService.deleteWishlist(request, member.getId());

        return ApiResponse.of(HttpStatus.OK, null, "관심 경매 삭제 성공", result);
    }
}
