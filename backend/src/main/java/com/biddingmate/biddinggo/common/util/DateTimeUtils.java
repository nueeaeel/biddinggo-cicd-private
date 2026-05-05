package com.biddingmate.biddinggo.common.util;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class DateTimeUtils {
    /**
     * ISO-8601 문자열을 시스템 로컬(한국) LocalDateTime으로 변환
     * 예: 2026-02-27T19:30:30+09:00 -> 2026-02-27T19:30:30
     */
    public static LocalDateTime toLocalDateTime(String isoOffsetDateTime) {
        if (isoOffsetDateTime == null || isoOffsetDateTime.isBlank()) {
            return null;
        }

        // 1. OffsetDateTime으로 파싱 (타임존 정보 유지)
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(isoOffsetDateTime);

        // 2. 시스템 기본 타임존(한국)의 LocalDateTime으로 변환
        // 만약 서버 환경이 한국 시간대라면 .toLocalDateTime()만 해도 19:30:30이 나옵니다.
        return offsetDateTime.toLocalDateTime();
    }
}
