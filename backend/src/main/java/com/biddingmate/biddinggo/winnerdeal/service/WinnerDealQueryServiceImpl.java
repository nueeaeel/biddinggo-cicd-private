package com.biddingmate.biddinggo.winnerdeal.service;

import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.response.PageResponse;
import com.biddingmate.biddinggo.review.mapper.ReviewMapper;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealDetailQueryResult;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.AdminWinnerDealListResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealDetailQueryResult;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealDetailResponse;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryRequest;
import com.biddingmate.biddinggo.winnerdeal.dto.WinnerDealHistoryResponse;
import com.biddingmate.biddinggo.winnerdeal.mapper.WinnerDealMapper;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDeal;
import com.biddingmate.biddinggo.winnerdeal.model.WinnerDealStatus;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WinnerDealQueryServiceImpl implements WinnerDealQueryService {
    private final WinnerDealMapper winnerDealMapper;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WinnerDeal> findByWinnerId(Long memberId) {
        return winnerDealMapper.findByWinnerId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WinnerDeal> findBySellerId(Long memberId) {
        return winnerDealMapper.findBySellerId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WinnerDealHistoryResponse> findPurchaseHistory(WinnerDealHistoryRequest request, Long memberId) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());

        List<WinnerDealHistoryResponse> content =
                winnerDealMapper.findPurchaseHistory(rowBounds, request, memberId);

        long totalElements =
                winnerDealMapper.countPurchaseHistory(request, memberId);

        return PageResponse.of(content, request.getPage(), request.getSize(), totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<WinnerDealHistoryResponse> findSaleHistory(WinnerDealHistoryRequest request, Long memberId) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());

        List<WinnerDealHistoryResponse> content =
                winnerDealMapper.findSaleHistory(rowBounds, request, memberId);

        long totalElements =
                winnerDealMapper.countSaleHistory(request, memberId);

        return PageResponse.of(content, request.getPage(), request.getSize(), totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public WinnerDealDetailResponse findWinnerDealDetail(Long winnerDealId, Long memberId) {
        WinnerDealDetailQueryResult detail = winnerDealMapper.findWinnerDealDetail(winnerDealId);
        if (detail == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        boolean isBuyer = detail.getWinnerId().equals(memberId);
        boolean isSeller = detail.getSellerId().equals(memberId);

        // 구매자 또는 판매자 구별
        if (!isBuyer && !isSeller) {
            throw new CustomException(ErrorType.WINNER_DEAL_ACCESS_DENIED);
        }

        WinnerDealStatus status = detail.getStatus();
        // 배송지 등록 여부
        boolean shippingAddressRegistered = isShippingAddressRegistered(detail);
        // 운송장 등록 여부
        boolean trackingNumberRegistered = isTrackingNumberRegistered(detail);
        // 구매자가 해당 낙찰 거래에 리뷰를 이미 작성했는지 확인
        boolean reviewWritten = reviewMapper.countByDealIdAndWriterId(detail.getWinnerDealId(), memberId) > 0;

        return WinnerDealDetailResponse.builder()
                .winnerDealId(detail.getWinnerDealId())
                .dealNumber(detail.getDealNumber())
                .auctionId(detail.getAuctionId())
                .itemId(detail.getItemId())
                .viewerRole(isBuyer ? "BUYER" : "SELLER")
                .itemName(detail.getItemName())
                .itemImageUrl(detail.getItemImageUrl())
                .status(status)
                .winnerPrice(detail.getWinnerPrice())
                .sellerName(detail.getSellerName())
                .winnerName(detail.getWinnerName())
                .recipient(detail.getRecipient())
                .tel(detail.getTel())
                .zipcode(detail.getZipcode())
                .address(detail.getAddress())
                .detailAddress(detail.getDetailAddress())
                .carrier(detail.getCarrier())
                .trackingNumber(detail.getTrackingNumber())
                .canRegisterShippingAddress(isBuyer && !shippingAddressRegistered && status == WinnerDealStatus.PAID)
                .canRegisterTrackingNumber(isSeller && shippingAddressRegistered && !trackingNumberRegistered && status == WinnerDealStatus.PAID)
                .canConfirmPurchase(isBuyer && status == WinnerDealStatus.SHIPPED && detail.getConfirmedAt() == null)
                .canWriteReview(isBuyer && status == WinnerDealStatus.CONFIRMED && !reviewWritten)
                .confirmedAt(detail.getConfirmedAt())
                .createdAt(detail.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminWinnerDealListResponse> findAdminWinnerDealHistory(AdminWinnerDealListRequest request) {
        String order = request.getOrder();
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }

        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String sortOrder = order.toUpperCase();

        List<AdminWinnerDealListResponse> content =
                winnerDealMapper.findAdminWinnerDealHistory(rowBounds, request, sortOrder);

        long totalElements = winnerDealMapper.countAdminWinnerDealHistory(request);

        return PageResponse.of(content, request.getPage(), request.getSize(), totalElements);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminWinnerDealDetailResponse findAdminWinnerDealDetail(Long winnerDealId) {
        AdminWinnerDealDetailQueryResult detail = winnerDealMapper.findAdminWinnerDealDetail(winnerDealId);
        if (detail == null) {
            throw new CustomException(ErrorType.WINNER_DEAL_NOT_FOUND);
        }

        boolean inspectionItem = detail.getInspectionYn() == YesNo.YES;
        boolean shippingAddressRegistered = isShippingAddressRegistered(detail);
        boolean trackingNumberRegistered = isTrackingNumberRegistered(detail);

        return AdminWinnerDealDetailResponse.builder()
                .winnerDealId(detail.getWinnerDealId())
                .dealNumber(detail.getDealNumber())
                .auctionId(detail.getAuctionId())
                .itemId(detail.getItemId())
                .itemName(detail.getItemName())
                .itemImageUrl(detail.getItemImageUrl())
                .auctionType(detail.getAuctionType())
                .inspectionYn(detail.getInspectionYn())
                .inspectionItem(inspectionItem)
                .status(detail.getStatus())
                .winnerPrice(detail.getWinnerPrice())
                .sellerName(detail.getSellerName())
                .winnerName(detail.getWinnerName())
                .recipient(detail.getRecipient())
                .tel(detail.getTel())
                .zipcode(detail.getZipcode())
                .address(detail.getAddress())
                .detailAddress(detail.getDetailAddress())
                .carrier(detail.getCarrier())
                .trackingNumber(detail.getTrackingNumber())
                .canRegisterTrackingNumber(inspectionItem
                        && detail.getStatus() == WinnerDealStatus.PAID
                        && shippingAddressRegistered
                        && !trackingNumberRegistered)
                .confirmedAt(detail.getConfirmedAt())
                .createdAt(detail.getCreatedAt())
                .build();
    }

    private boolean isShippingAddressRegistered(WinnerDealDetailQueryResult detail) {
        // null, 빈 문자열, 공백만 있는 값은 미입력으로 본다.
        return StringUtils.hasText(detail.getRecipient())
                && StringUtils.hasText(detail.getTel())
                && StringUtils.hasText(detail.getZipcode())
                && StringUtils.hasText(detail.getAddress());
    }

    private boolean isTrackingNumberRegistered(WinnerDealDetailQueryResult detail) {
        // null, 빈 문자열, 공백만 있는 값은 미입력으로 본다.
        return StringUtils.hasText(detail.getCarrier())
                && StringUtils.hasText(detail.getTrackingNumber());
    }

    private boolean isShippingAddressRegistered(AdminWinnerDealDetailQueryResult detail) {
        return StringUtils.hasText(detail.getRecipient())
                && StringUtils.hasText(detail.getTel())
                && StringUtils.hasText(detail.getZipcode())
                && StringUtils.hasText(detail.getAddress());
    }

    private boolean isTrackingNumberRegistered(AdminWinnerDealDetailQueryResult detail) {
        return StringUtils.hasText(detail.getCarrier())
                && StringUtils.hasText(detail.getTrackingNumber());
    }
}
