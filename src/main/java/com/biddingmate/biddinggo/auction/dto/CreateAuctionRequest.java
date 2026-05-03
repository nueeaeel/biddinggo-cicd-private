package com.biddingmate.biddinggo.auction.dto;

import com.biddingmate.biddinggo.auction.model.AuctionType;
import com.biddingmate.biddinggo.auction.model.YesNo;
import com.biddingmate.biddinggo.item.dto.AuctionItemCreateSource;
import com.biddingmate.biddinggo.item.dto.ItemImageCreateSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "경매 등록 요청 DTO")
public class CreateAuctionRequest {
    @Schema(description = "경매 상품 정보")
    @NotNull(message = "상품 정보는 필수입니다.")
    @Valid
    private Item item;

    @Schema(description = "경매 정보")
    @NotNull(message = "경매 정보는 필수입니다.")
    @Valid
    private Auction auction;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "경매 상품 요청 DTO")
    public static class Item implements AuctionItemCreateSource {
        @Schema(description = "카테고리 ID", example = "10")
        @NotNull(message = "카테고리 ID는 필수입니다.")
        private Long categoryId;

        @Schema(description = "브랜드명", example = "Nike")
        @Size(max = 50, message = "브랜드명은 50자 이하여야 합니다.")
        private String brand;

        @Schema(description = "상품명", example = "Air Jordan 1 High")
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 50, message = "상품명은 50자 이하여야 합니다.")
        private String name;

        @Schema(description = "상품 상태", example = "최상")
        @Size(max = 20, message = "상품 상태는 20자 이하여야 합니다.")
        private String quality;

        @Schema(description = "상품 설명", example = "실착 2회, 박스 포함")
        private String description;

        @Schema(description = "상품 이미지 목록")
        @Size(max = 10, message = "상품 이미지는 최대 10장까지 등록할 수 있습니다.")
        private List<@Valid @NotNull(message = "이미지 정보는 null일 수 없습니다.") Image> images;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "상품 이미지 요청 DTO")
    public static class Image implements ItemImageCreateSource {
        @Schema(description = "R2에 업로드된 임시 파일 key", example = "temp/items/2026/03/13/uuid.jpg")
        @NotBlank(message = "파일 key는 필수입니다.")
        private String fileKey;

        @Schema(description = "이미지 노출 순서", example = "1")
        @NotNull(message = "이미지 노출 순서는 필수입니다.")
        @Positive(message = "이미지 노출 순서는 1 이상이어야 합니다.")
        private Integer displayOrder;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "경매 요청 DTO")
    public static class Auction {
        @Schema(description = "경매 타입", example = "NORMAL")
        private AuctionType type;

        @Schema(description = "시작가", example = "100000")
        @PositiveOrZero(message = "시작가는 0 이상이어야 합니다.")
        private Long startPrice;

        @Schema(description = "입찰 단위", example = "1000")
        @Positive(message = "입찰 단위는 1 이상이어야 합니다.")
        private Integer bidUnit;

        @Schema(description = "비크리 가격", example = "150000", nullable = true)
        @PositiveOrZero(message = "비크리 가격은 0 이상이어야 합니다.")
        private Long vickreyPrice;

        @Schema(description = "즉시 구매가", example = "180000", nullable = true)
        @PositiveOrZero(message = "즉시 구매가는 0 이상이어야 합니다.")
        private Long buyNowPrice;

        @Schema(description = "연장 플래그 (NO: 연장경매 허용, YES: 연장경매 미허용)", example = "NO", nullable = true)
        private YesNo extensionYn;

        @Schema(description = "경매 시작일시", example = "2026-03-15T10:00:00")
        @NotNull(message = "경매 시작일시는 필수입니다.")
        private LocalDateTime startDate;

        @Schema(description = "경매 종료일시", example = "2026-03-16T10:00:00")
        @NotNull(message = "경매 종료일시는 필수입니다.")
        private LocalDateTime endDate;
    }
}
