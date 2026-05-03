package com.biddingmate.biddinggo.payment.mapper;

import com.biddingmate.biddinggo.common.inif.IMybatisCRUD;
import com.biddingmate.biddinggo.payment.dto.PaymentInfo;
import com.biddingmate.biddinggo.payment.model.Payment;
import com.biddingmate.biddinggo.payment.model.PaymentStatus;
import com.biddingmate.biddinggo.payment.dto.GetVirtualAccountResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentMapper extends IMybatisCRUD<Payment> {
    boolean existsByOrderId(@Param("orderId") String orderId);

    List<GetVirtualAccountResponse> findByMemberId(@Param("memberId") Long memberId,
                                                   @Param("status") PaymentStatus status);

    int completeIfWaiting(@Param("orderId") String orderId,
                          @Param("status") PaymentStatus status,
                          @Param("approvedAt") LocalDateTime approvedAt);

    PaymentInfo findPaymentInfoByOrderId(@Param("orderId") String orderId);
}
