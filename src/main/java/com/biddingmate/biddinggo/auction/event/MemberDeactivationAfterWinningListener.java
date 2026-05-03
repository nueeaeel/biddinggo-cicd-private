package com.biddingmate.biddinggo.auction.event;

import com.biddingmate.biddinggo.member.event.MemberStatusUpdateEvent;
import com.biddingmate.biddinggo.member.model.MemberStatus;
import com.biddingmate.biddinggo.winnerdeal.service.WinnerDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(2)
public class MemberDeactivationAfterWinningListener {

    private final WinnerDealService winnerDealService;

    @EventListener
    @Transactional
    public void handle(MemberStatusUpdateEvent event) {
        if (event.getStatus() == MemberStatus.INACTIVE) {
            winnerDealService.handleMemberDeactivationAfterWinning(event.getMemberId());
        }
    }
}
