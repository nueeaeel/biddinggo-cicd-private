package com.biddingmate.biddinggo.report.service;

import com.biddingmate.biddinggo.report.dto.ReportCreateRequest;

public interface ReportService {

    void processReport(ReportCreateRequest request, Long reporterId);
}