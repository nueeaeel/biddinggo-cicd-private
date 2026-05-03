package com.biddingmate.biddinggo.auction.prediction.event;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionPriceReferenceSyncCommand;
import com.biddingmate.biddinggo.auction.prediction.policy.ConditionScorePolicy;
import com.biddingmate.biddinggo.auction.prediction.service.AuctionEmbeddingTextAssembler;
import com.biddingmate.biddinggo.auction.prediction.service.AuctionPriceReferenceSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 경매 낙찰 완료 커밋 이후 reference 동기화 요청을 실제 동기화 커맨드로 변환하는 리스너.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionPriceReferenceSyncRequestedListener {
    private final AuctionEmbeddingTextAssembler auctionEmbeddingTextAssembler;
    private final ConditionScorePolicy conditionScorePolicy;
    private final AuctionPriceReferenceSyncService auctionPriceReferenceSyncService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuctionPriceReferenceSyncRequestedEvent event) {
        String embeddingText = auctionEmbeddingTextAssembler.assemble(
                event.getBrand(),
                event.getName(),
                event.getDescription()
        );

        if (embeddingText.isBlank()) {
            log.warn("Skip auction price reference sync because embedding text is blank. auctionId={}, itemId={}", event.getAuctionId(), event.getItemId());
            return;
        }

        AuctionPriceReferenceSyncCommand command = AuctionPriceReferenceSyncCommand.builder()
                .auctionId(event.getAuctionId())
                .itemId(event.getItemId())
                .categoryId(event.getCategoryId())
                .quality(event.getQuality())
                .conditionScore(conditionScorePolicy.resolve(event.getQuality()))
                .embeddingText(embeddingText)
                .winnerPrice(event.getWinnerPrice())
                .completedAt(event.getCompletedAt())
                .build();

        auctionPriceReferenceSyncService.sync(command);
    }
}
