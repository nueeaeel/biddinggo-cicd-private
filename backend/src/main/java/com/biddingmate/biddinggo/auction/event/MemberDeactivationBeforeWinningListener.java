package com.biddingmate.biddinggo.auction.event;

import com.biddingmate.biddinggo.auction.service.AuctionService;
import com.biddingmate.biddinggo.member.event.MemberStatusUpdateEvent;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(1)
public class MemberDeactivationBeforeWinningListener {

    private final AuctionService auctionService;

    @EventListener
    @Transactional
    public void handle(MemberStatusUpdateEvent event) {
        if (event.getStatus() == MemberStatus.INACTIVE) {
            auctionService.handleMemberDeactivationBeforeWinning(event.getMemberId());
        }
    }
}
