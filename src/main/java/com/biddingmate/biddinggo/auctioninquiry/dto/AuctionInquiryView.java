package com.biddingmate.biddinggo.auctioninquiry.dto;

import com.biddingmate.biddinggo.auctioninquiry.model.AuctionInquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class AuctionInquiryView {
    private final Long id;
    private final Long writerId;
    private final String title;
    private final String content;
    private final String writerName;
    private final String answer;
    private final LocalDateTime answeredAt;
    private final boolean secretYn;
    private final AuctionInquiryStatus status;

    private final LocalDateTime createdAt;

    // 닉네임 마스킹 처리된 '새 객체' 반환
    public AuctionInquiryView withMaskedWriterName() {
        String maskedName;
        if (this.writerName != null && this.writerName.length() >= 3) {
            maskedName = this.writerName.substring(0, 3) + "***";
        } else {
            maskedName = (this.writerName == null || this.writerName.isEmpty()) ? "익명***" : this.writerName + "***";
        }
        return this.toBuilder().writerName(maskedName).build();
    }

    // 비밀글 마스킹 처리된 '새 객체' 반환
    public AuctionInquiryView withSecretMasking() {
        return this.toBuilder()
                .title("비밀글입니다.")
                .content("작성자와 판매자만 볼 수 있는 문의입니다.")
                .answer(this.answer != null ? "비밀 답변입니다." : null)
                .build();
    }
}
