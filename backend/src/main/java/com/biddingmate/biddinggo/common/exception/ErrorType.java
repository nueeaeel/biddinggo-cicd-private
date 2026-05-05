package com.biddingmate.biddinggo.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    // 공통
    BAD_REQUEST("bad_request", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("unauthorized", "인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("forbidden", "권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("not_found", "리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CONFLICT("conflict", "요청이 현재 상태와 충돌합니다.", HttpStatus.CONFLICT),
    INTERNAL_ERROR("internal_error", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 페이징 처리
    INVALID_SORT_ORDER("paging-001", "정렬 방향이 존재하질 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_SORT_BY("paging-002", "정렬 기준이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 도메인 별 예시
    // auth
    EXPIRED_ACCESS_TOKEN("auth-001", "만료된 Access Token 입니다.", HttpStatus.CONFLICT),
    INVALID_TOKEN("auth-002", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("auth-003", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_FOUND("auth-004", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("auth-005","아이디 또는 비밀번호가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATE_USERNAME("auth-006","이미 가입된 아이디입니다.",HttpStatus.BAD_REQUEST),
    DUPLICATE_EMAIL("auth-007","이미 가입된 이메일입니다.",HttpStatus.BAD_REQUEST),
    DUPLICATE_NICKNAME("auth-008","이미 사용 중인 닉네임입니다.",HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_INVALID("auth-009","리프레쉬 토큰이 유효하지 않습니다.",HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("auth-010","필수 정보를 입력할 사용자를 찾지 못했습니다.",HttpStatus.NOT_FOUND),
    REDIS_UNAVAILABLE("auth-011", "인증 저장소 연결에 실패했습니다. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE),
    ALREADY_REGISTERED_USER("auth-012", "이미 필수 정보 등록이 완료된 사용자입니다.", HttpStatus.BAD_REQUEST),


    // 결제
    VIRTUAL_ACCOUNT_ALREADY_EXISTS("payment-001", "가상계좌가 이미 존재합니다.", HttpStatus.CONFLICT),
    TOSS_API_CLIENT_ERROR("payment-002", "토스 API 요청이 잘못되었습니다. 요청 파라미터와 인증 정보를 확인하세요.", HttpStatus.BAD_REQUEST),
    TOSS_API_SERVER_ERROR("payment-003", "토스 서버 내부 오류가 발생했습니다. 잠시 후 다시 시도하세요.", HttpStatus.BAD_GATEWAY),
    PAYMENT_NOT_FOUND("payment-004", "해당 주문의 결제 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 포인트 히스토리
    POINT_HISTORY_SAVE_FAILED("point-history-001", "포인트 히스토리 저장 실패", HttpStatus.INTERNAL_SERVER_ERROR),

    // 경매
    INVALID_AUCTION_CREATE_REQUEST("auction-001", "경매 등록 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_ITEM_SAVE_FAILED("auction-002", "경매 상품 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    AUCTION_SAVE_FAILED("auction-003", "경매 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ITEM_IMAGE_SAVE_FAILED("auction-004", "상품 이미지 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    DUPLICATE_ITEM_IMAGE_DISPLAY_ORDER("auction-005", "상품 이미지 노출 순서는 중복될 수 없습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_FOUND("auction-006", "경매를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND("auction-007", "카테고리를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_CATEGORY_LEVEL("auction-008", "최하위 카테고리만 선택할 수 있습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_ITEM_NOT_FOUND("auction-009", "경매 등록 대상 상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    AUCTION_ITEM_SELLER_MISMATCH("auction-010", "상품 판매자와 요청 판매자가 일치하지 않습니다.", HttpStatus.FORBIDDEN),
    INSPECTION_NOT_PASSED("auction-011", "검수 완료된 상품만 경매 등록할 수 있습니다.", HttpStatus.CONFLICT),
    ITEM_NOT_AUCTIONABLE("auction-012", "현재 상태에서는 경매 등록할 수 없습니다.", HttpStatus.CONFLICT),
    INVALID_AUCTION_UPDATE_REQUEST("auction-013", "경매 수정 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AUCTION_CANCEL_REQUEST("auction-014", "경매 취소 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_UPDATE_NOT_ALLOWED("auction-015", "현재 상태에서는 경매를 수정할 수 없습니다.", HttpStatus.CONFLICT),
    AUCTION_CANCEL_NOT_ALLOWED("auction-016", "현재 상태에서는 경매를 취소할 수 없습니다.", HttpStatus.CONFLICT),
    INVALID_AUCTION_STATUS("auction-017", "유효하지 않은 경매 상태값입니다.", HttpStatus.BAD_REQUEST),

    // 검수
    INVALID_INSPECTION_CREATE_REQUEST("inspection-001", "검수 등록 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INSPECTION_SAVE_FAILED("inspection-002", "검수 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INSPECTION_NOT_FOUND("inspection-003", "검수 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_INSPECTION_STATUS("inspection-004", "현재 검수 상태에서는 처리할 수 없습니다.", HttpStatus.CONFLICT),
    INSPECTION_SHIPPING_INFO_ALREADY_EXISTS("inspection-005", "이미 배송 정보가 등록된 검수입니다.", HttpStatus.CONFLICT),
    INSPECTION_SHIPPING_UPDATE_FAILED("inspection-006", "검수 배송 정보 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_INSPECTION_LIST_REQUEST("inspection-007", "검수물품 목록 조회 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_INSPECTION_LIST_STATUS("inspection-008", "검수물품 상태값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_INSPECTION_STATUS("inspection-009", "이미 검수 완료된 물품입니다.", HttpStatus.CONFLICT),
    INSPECTION_SHIPPING_INFO_REQUIRED("inspection-010", "운송장 정보가 등록된 검수만 처리할 수 있습니다.", HttpStatus.CONFLICT),

    // 파일
    INVALID_FILE_UPLOAD_REQUEST("file-001", "파일 업로드 요청이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    R2_PRESIGNED_URL_GENERATION_FAILED("file-002", "R2 업로드 URL 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_DELETE_FAILED("file-003", "파일 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_LOOKUP_FAILED("file-004", "업로드된 파일 조회에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    UPLOADED_FILE_NOT_FOUND("file-005", "업로드된 파일을 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_UPLOADED_FILE_METADATA("file-006", "업로드된 파일 메타데이터가 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 1대1 문의
    ADMIN_INQUIRY_CREATED_FAIL("admin-inquiry-001", "관리자 1대1 문의 생성 실패" , HttpStatus.INTERNAL_SERVER_ERROR),
    ADMIN_INQUIRY_UPDATED_FAIL("admin-inquiry-002", "관리자 1대1 문의 답변 실패" , HttpStatus.INTERNAL_SERVER_ERROR),
    ADMIN_INQUIRY_NOT_FOUND("admin-inquiry-003", "해당 1대1 문의가 존재하질 않습니다.", HttpStatus.NOT_FOUND),
    ADMIN_INQUIRY_ALREADY_ANSWERED("admin-inquiry-004", "해당 1대1 문의는 이미 답변이 완료된 상태입니다.", HttpStatus.CONFLICT),

    // 배송지 관리
    ADDRESS_CREATED_FAIL("address-001", "배송지 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_MAX_COUNT_EXCEEDED("address-002", "배송지는 최대 3개까지 등록 가능합니다.", HttpStatus.CONFLICT),
    ADDRESS_NOT_FOUND("address-003", "해당 배송지는 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ADDRESS_UPDATE_DEFAULT_FAIL("address-004", "기본 배송지 변경에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_DELETE_DEFAULT_FAIL("address-005", "배송지 삭제를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 경매 문의
    AUCTION_INQUIRY_CONTENT_INVALID("auction-inquiry-001", "문의 내용이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_INQUIRY_CREATE_FAIL("auction-inquiry-002", "경매 문의 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    CANNOT_INQUIRE_OWN_AUCTION("auction-inquiry-003", "본인이 등록한 경매에는 문의할 수 없습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_INQUIRY_NOT_FOUND("auction-inquiry-004", "해당 경매 문의가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    AUCTION_INQUIRY_ALREADY_ANSWERED("auction-inquiry-005", "이미 답변이 등록된 문의입니다.", HttpStatus.CONFLICT),
    AUCTION_INQUIRY_UPDATE_FAIL("auction-inquiry-006", "경매 문의 답변 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    INQUIRY_ONLY_FOR_WINNER("auction-inquiry-007", "구매자만 판매자에게 문의를 남길 수 있습니다.", HttpStatus.FORBIDDEN),

    // 회원 정보
    MEMBER_NOT_FOUND("member-001", "존재하지 않는 회원입니다.", HttpStatus.NOT_FOUND),
    INVALID_NICKNAME_CHANGE_PERIOD("member-002", "닉네임은 30일 이후에 변경할 수 있습니다.", HttpStatus.BAD_REQUEST),
    DUPLICATED_NICKNAME("member-003", "이미 사용 중인 닉네임입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_DELETED_MEMBER("member-004", "탈퇴한 회원입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ENUM_TYPE("member-005", "유효한 상태값이 아닙니다.", HttpStatus.CONFLICT),

    // 입찰
    BID_SAVE_FAILED("bid-001", "입찰 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    BID_AMOUNT_TOO_LOW("bid-002", "입찰 금액이 최소 입찰 가능 금액보다 낮습니다.", HttpStatus.BAD_REQUEST),
    INVALID_BID_UNIT("bid-003", "입찰 단위로만 입찰이 가능합니다.", HttpStatus.BAD_REQUEST),
    BID_AMOUNT_NOT_HIGHER_THAN_PREVIOUS("bid-004", "이전 입찰 기록보다 높게 설정해주세요.", HttpStatus.BAD_REQUEST),
    AUCTION_NOT_BIDDABLE("bid-005", "진행중인 경매에만 입찰을 등록할 수 있습니다.", HttpStatus.BAD_REQUEST),
    NOT_ENOUGH_POINT("bid-006", "보유 포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    CANNOT_BID_ON_OWN_AUCTION("bid-007", "자신의 경매에 입찰할 수 없습니다.", HttpStatus.BAD_REQUEST),
    AUCTION_ALREADY_FINISHED("bid-008", "이미 종료된 경매이거나 연장할 수 없는 상태입니다.", HttpStatus.CONFLICT),
    CANNOT_BUY_NOW("bid-009", "해당 경매는 현재 즉시구매할 수 없습니다.", HttpStatus.CONFLICT),
    CANNOT_BUY_NOW_OWN_AUCTION("bid-010", "자신의 경매는 즉시구매할 수 없습니다.", HttpStatus.CONFLICT),

    // 관심 경매
    WISHLIST_SAVE_FAIL("wishlist-001", "관심 경매 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WISHLIST_ALREADY_EXISTS("wishlist-002", "해당 관심 경매가 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    WISHLIST_NOT_FOUND("wishlist-003", "해당 관심 경매가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    WISHLIST_DELETE_FAIL("wishlist-004", "관심 경매 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 낙찰
    WINNER_DEAL_ALREADY_CANCELLED("winner-deal-001", "이미 취소된 낙찰 거래입니다.", HttpStatus.CONFLICT),
    WINNER_DEAL_UPDATE_FAILED("winner-deal-002", "낙찰 거래 상태 변경에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WINNER_DEAL_NOT_FOUND("winner-deal-003", "해당 경매에 대한 낙찰 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WINNER_DEAL_ACCESS_DENIED("winner-deal-004", "본인의 낙찰 거래만 조회할 수 있습니다.", HttpStatus.FORBIDDEN),
    WINNER_DEAL_SHIPPING_ADDRESS_ACCESS_DENIED("winner-deal-005", "낙찰 거래의 구매자만 배송지 정보를 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    WINNER_DEAL_SHIPPING_ADDRESS_REGISTRATION_NOT_ALLOWED("winner-deal-006", "현재 상태의 낙찰 거래에는 배송지 정보를 등록할 수 없습니다.", HttpStatus.CONFLICT),
    WINNER_DEAL_SHIPPING_ADDRESS_SAVE_FAILED("winner-deal-007", "낙찰 거래 배송지 정보 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WINNER_DEAL_TRACKING_NUMBER_ACCESS_DENIED("winner-deal-008", "낙찰 거래의 판매자만 운송장 정보를 등록할 수 있습니다.", HttpStatus.FORBIDDEN),
    WINNER_DEAL_TRACKING_NUMBER_REGISTRATION_NOT_ALLOWED("winner-deal-009", "현재 상태의 낙찰 거래에는 운송장 정보를 등록할 수 없습니다.", HttpStatus.CONFLICT),
    WINNER_DEAL_TRACKING_NUMBER_SAVE_FAILED("winner-deal-010", "낙찰 거래 운송장 정보 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WINNER_DEAL_CONFIRM_ACCESS_DENIED("winner-deal-011", "낙찰 거래의 구매자만 구매확정을 할 수 있습니다.", HttpStatus.FORBIDDEN),
    WINNER_DEAL_CONFIRM_NOT_ALLOWED("winner-deal-012", "현재 상태의 낙찰 거래는 구매확정을 할 수 없습니다.", HttpStatus.CONFLICT),
    WINNER_DEAL_CONFIRM_FAILED("winner-deal-013", "낙찰 거래 구매확정 처리에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    WINNER_DEAL_SETTLEMENT_INVALID("winner-deal-014", "낙찰 거래 정산 정보가 올바르지 않습니다.", HttpStatus.CONFLICT),
    WINNER_DEAL_BID_AMOUNT_NOT_FOUND("winner-deal-015", "낙찰자의 예치 입찰금을 찾을 수 없습니다.", HttpStatus.CONFLICT),

    // 리뷰
    REVIEW_SAVE_FAIL("review-001", "리뷰 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_REVIEWED("review-002", "이미 해당 거래에 대한 리뷰를 작성했습니다.", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND("review-003", "해당 리뷰가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    REVIEW_DELETE_FAIL("review-004", "리뷰 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_THE_WINNER("review-005", "낙찰자만 해당 경매의 리뷰를 남길 수 있습니다.", HttpStatus.FORBIDDEN),
    SELLER_NOT_FOUND("review-006", "해당 경매의 판매자 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    WINNER_DEAL_NOT_CONFIRMED("review-007", "구매 확정된 거래에 대해서만 리뷰를 작성할 수 있습니다.", HttpStatus.BAD_REQUEST),

    // 알림
    NOTIFICATION_SAVE_FAILED("notification-001", "알림 등록에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 공지사항
    NOTICE_NOT_FOUND("notice-001", "해당 공지사항을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOTICE_CREATE_FAIL("notice-002", "공지사항 등록을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTICE_UPDATE_FAIL("notice-003", "공지사항 수정을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOTICE_DELETE_FAIL("notice-004", "공지사항 삭제를 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_NOTICE_REQUEST("notice-005", "제목 또는 내용의 형식 오류입니다.", HttpStatus.BAD_REQUEST),

    // 회원 탈퇴
    CANNOT_DELETE_MEMBER_WITH_ONGOING_SALES("account-001","판매 중인 경매가 존재하여 탈퇴할 수 없습니다.", HttpStatus.CONFLICT),
    CANNOT_DELETE_MEMBER_WITH_ONGOING_BIDS("account-002","입찰 중인 경매가 존재하여 탈퇴할 수 없습니다.", HttpStatus.CONFLICT),
    CANNOT_DELETE_MEMBER_WITH_INCOMPLETE_DEALS("account-003","진행 중인 거래가 존재하여 탈퇴할 수 없습니다.", HttpStatus.CONFLICT),

    // 신고
    CANNOT_REPORT_SELF("report-001", "자기 자신은 신고할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_REPORT_TARGET("report-002", "신고 대상이 올바르지 않습니다.", HttpStatus.NOT_FOUND),
    REPORT_CREATE_FAIL("report-003", "신고 접수에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_REPORTED("report-004", "이미 신고한 사용자입니다.", HttpStatus.CONFLICT);

    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}
