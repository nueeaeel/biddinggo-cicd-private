package com.biddingmate.biddinggo.common.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * 공용 페이징 요청 DTO.
 *
 * <p>컨트롤러의 query parameter를 바인딩해 사용하며,
 * 공통 페이징 요소인 page/size와 JPA/MyBatis 변환 헬퍼만 제공한다.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "공용 페이징 요청 DTO")
public class BasePageRequest {
    private static final int MAX_SIZE = 100;

    @Schema(description = "1부터 시작하는 페이지 번호", example = "1", defaultValue = "1")
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    @Builder.Default
    protected Integer page = 1;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = MAX_SIZE, message = "페이지 크기는 100 이하여야 합니다.")
    @Builder.Default
    protected Integer size = 10;

    @Schema(description = "정렬 방향", example = "ASC", defaultValue = "ASC")
    @Builder.Default
    protected String order = "ASC";

    public int getOffset() {
        // page가 1부터 시작한다고 가정할 경우
        return (page - 1) * size;
    }
}
