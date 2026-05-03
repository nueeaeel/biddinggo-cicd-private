package com.biddingmate.biddinggo.auction.prediction.event;

import com.biddingmate.biddinggo.auction.prediction.model.AuctionQueryEmbeddingSyncCommand;
import com.biddingmate.biddinggo.auction.prediction.policy.ConditionScorePolicy;
import com.biddingmate.biddinggo.auction.prediction.service.AuctionEmbeddingTextAssembler;
import com.biddingmate.biddinggo.auction.prediction.service.AuctionQueryEmbeddingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 경매 등록/수정 커밋 이후 임베딩 동기화 요청을 실제 동기화 커맨드로 변환하는 리스너.
 * 현재 단계에서는 임베딩 원문 조립과 condition score 계산까지만 담당한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionQueryEmbeddingSyncRequestedListener {
    private final AuctionEmbeddingTextAssembler auctionEmbeddingTextAssembler;
    private final ConditionScorePolicy conditionScorePolicy;
    private final AuctionQueryEmbeddingSyncService auctionQueryEmbeddingSyncService;

    /**
     * 커밋이 완료된 뒤에만 동작하여 본 트랜잭션과 외부 연동을 분리한다.
     * 이벤트 payload를 기반으로 임베딩 원문과 메타데이터를 조합한 뒤 동기화 서비스로 넘긴다.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuctionQueryEmbeddingSyncRequestedEvent event) {
        String embeddingText = auctionEmbeddingTextAssembler.assemble(
                event.getBrand(),
                event.getName(),
                event.getDescription()
        );

        if (embeddingText.isBlank()) {
            log.warn("Skip auction query embedding sync because embedding text is blank. auctionId={}, itemId={}", event.getAuctionId(), event.getItemId());
            return;
        }

        AuctionQueryEmbeddingSyncCommand command = AuctionQueryEmbeddingSyncCommand.builder()
                .auctionId(event.getAuctionId())
                .itemId(event.getItemId())
                .categoryId(event.getCategoryId())
                .quality(event.getQuality())
                .conditionScore(conditionScorePolicy.resolve(event.getQuality()))
                .embeddingText(embeddingText)
                .trigger(event.getTrigger())
                .build();

        auctionQueryEmbeddingSyncService.sync(command);
    }
}
