package com.biddingmate.biddinggo.bid.service;

import com.biddingmate.biddinggo.auction.model.Auction;
import com.biddingmate.biddinggo.bid.dto.BidListResponse;
import com.biddingmate.biddinggo.bid.dto.BidMaskingResponse;
import com.biddingmate.biddinggo.bid.dto.BidResponse;
import com.biddingmate.biddinggo.bid.dto.CreateBidRequest;
import com.biddingmate.biddinggo.bid.mapper.BidMapper;
import com.biddingmate.biddinggo.bid.model.Bid;
import com.biddingmate.biddinggo.common.exception.CustomException;
import com.biddingmate.biddinggo.common.exception.ErrorType;
import com.biddingmate.biddinggo.common.request.BasePageRequest;
import com.biddingmate.biddinggo.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BidServiceImpl implements BidService {
    private final BidMapper bidMapper;

    @Override
    public Bid createBid(Long memberId, Auction auction, CreateBidRequest request) {


        int bidCount = bidMapper.getBidCount(Map.of("auctionId", auction.getId()));
        Long minBidAmount;

        if (bidCount == 0) {
            minBidAmount = auction.getStartPrice();
        } else if (bidCount == 1) {
            minBidAmount = auction.getStartPrice() + auction.getBidUnit();
        } else {
            Bid vickreyBid = bidMapper.getVickreyBid(auction.getId());
            minBidAmount = vickreyBid.getAmount() + auction.getBidUnit();
        }

        if (minBidAmount > request.getAmount()) {
            throw new CustomException(ErrorType.BID_AMOUNT_TOO_LOW);
        }

        if((request.getAmount() - auction.getStartPrice()) % auction.getBidUnit() != 0){
            throw new CustomException(ErrorType.INVALID_BID_UNIT);
        }

        Long lastBidAmount = bidMapper.getLastBidAmountByMemberId(auction.getId(), memberId);
        if (lastBidAmount == null) {
            lastBidAmount = 0L;
        }

        if(lastBidAmount >= request.getAmount()){
            throw new CustomException(ErrorType.BID_AMOUNT_NOT_HIGHER_THAN_PREVIOUS);
        }

        // Bid 등록
        Bid bid = Bid.builder()
                .bidderId(memberId)
                .auctionId(auction.getId())
                .amount(request.getAmount())
                .createdAt(LocalDateTime.now())
                .build();

        int bidInsertCount = bidMapper.insert(bid);

        if(bidInsertCount != 1 || bid.getId() == null){
            throw new CustomException(ErrorType.BID_SAVE_FAILED);
        }

        return bid;
    }

    @Override
    public Long getLastBidAmount(Long memberId, Long auctionId) {
        Long lastBidAmount = bidMapper.getLastBidAmountByMemberId(auctionId, memberId);
        if (lastBidAmount == null) {
            lastBidAmount = 0L;
        }

        return lastBidAmount;
    }

    @Override
    public Bid getVickreyBid(Long auctionId) {
        return bidMapper.getVickreyBid(auctionId);
    }

    @Override
    public PageResponse<BidMaskingResponse> getBidsByAuctionId(BasePageRequest request, Long auctionId) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<BidResponse> bids = bidMapper.getBidsByAuctionId(rowBounds, auctionId, sortOrder);
        List<BidMaskingResponse> maskingBids = new ArrayList<>(bids.size());

        HashMap<Long, Long> maskingHm = new HashMap<>(); //key: id, value: cnt
        long cnt = 1L;
        for(BidResponse bid: bids) {
            if(!maskingHm.containsKey(bid.getBidderId())) {
                maskingHm.put(bid.getBidderId(), cnt++);
            }

            BidMaskingResponse maskingBid = BidMaskingResponse.builder()
                    .id(bid.getId())
                    .auctionId(bid.getAuctionId())
                    .bidderId(maskingHm.get(bid.getBidderId()))
                    .amount("***")
                    .createdAt(bid.getCreatedAt())
                    .build();

            maskingBids.add(maskingBid);
        }

        int bidCount = bidMapper.getBidCount(Map.of("auctionId", auctionId));

        return PageResponse.of(maskingBids, request.getPage(), request.getSize(), bidCount);
    }

    @Override
    public PageResponse<BidListResponse> getBidAuctionsByMemberId(BasePageRequest request, Long memberId) {
        RowBounds rowBounds = new RowBounds(request.getOffset(), request.getSize());
        String order = request.getOrder();

        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            throw new CustomException(ErrorType.INVALID_SORT_ORDER);
        }
        String sortOrder = order.toUpperCase();

        List<BidListResponse> bidList = bidMapper.findBidAuctionsByMemberId(rowBounds, memberId, sortOrder);
        int bidCount = bidMapper.getBidCount(Map.of("bidderId", memberId));

        return PageResponse.of(bidList, request.getPage(), request.getSize(), bidCount);
    }

    @Override
    @Transactional
    public void invalidateBidsByMember(Long memberId) {
        bidMapper.invalidateBidsByMember(memberId);
    }
}