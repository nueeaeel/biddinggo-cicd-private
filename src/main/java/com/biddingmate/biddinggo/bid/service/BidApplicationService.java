package com.biddingmate.biddinggo.bid.service;

import com.biddingmate.biddinggo.bid.dto.CreateBidRequest;
import com.biddingmate.biddinggo.bid.dto.CreateBidResponse;
import jakarta.validation.Valid;

/*
    입찰 유스케이스를 조합하는 상위 서비스
    컨트롤러는 이 서비스를 통해 입찰을 시작한다.
 */
public interface BidApplicationService {
    /*
        입찰, 경매 정보 수정, 사용자 정보 수정을 하나의 흐름으로 처리한다.
     */
    CreateBidResponse createBidProcess(Long memberId, @Valid CreateBidRequest request);
}
