package com.biddingmate.biddinggo.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 공용 페이징 응답 DTO.
 *
 * <p>페이지 번호는 0부터 시작하는 값을 기준으로 사용한다.</p>
 * <p>JPA에서는 {@code Page<T>}를 그대로 넘겨 생성할 수 있고,
 * MyBatis처럼 {@code List<T> + totalElements} 형태로 조회한 경우에도 생성할 수 있다.</p>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean first;
    private boolean last;
    private boolean empty;

    // 팩토리 메서드
    public static <T> PageResponse<T> of(
            List<T> content,
            int page,
            int size,
            long totalElements
    ) {
        if (page < 1) throw new IllegalArgumentException("page >= 1");
        if (size < 1) throw new IllegalArgumentException("size >= 1");
        if (totalElements < 0) throw new IllegalArgumentException("total >= 0");

        List<T> safeContent = content == null ? List.of() : List.copyOf(content);

        int totalPages = totalElements == 0 ? 0 :
                (int) Math.ceil((double) totalElements / size);

        int numberOfElements = safeContent.size();

        boolean first = page == 1;
        boolean last = totalPages == 0 || page >= totalPages;
        boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;
        boolean empty = safeContent.isEmpty();

        return new PageResponse<>(
                safeContent,
                page,
                size,
                totalElements,
                totalPages,
                numberOfElements,
                hasNext,
                hasPrevious,
                first,
                last,
                empty
        );
    }
}
