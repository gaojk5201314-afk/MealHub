package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@Service
public interface ReportService {
    TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end);
    UserReportVO userStatistics(LocalDate begin, LocalDate end);
    OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);
    SalesTop10ReportVO top10(LocalDate begin, LocalDate end);
    void exportBusinessDate(HttpServletResponse response);
}
