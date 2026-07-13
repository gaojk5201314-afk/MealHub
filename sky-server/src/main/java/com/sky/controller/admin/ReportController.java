package com.sky.controller.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags = "报表管理")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("营业额统计,开始时间: {},结束时间: {}", begin, end);
        TurnoverReportVO reportVO = reportService.turnoverStatistics(begin, end);
        return Result.success(reportVO);
    }
    
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计,开始时间: {},结束时间: {}", begin, end);
        UserReportVO reportVO = reportService.userStatistics(begin, end);
        return Result.success(reportVO);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计,开始时间: {},结束时间: {}", begin, end);
        OrderReportVO reportVO = reportService.ordersStatistics(begin, end);
        return Result.success(reportVO);
    }

    @GetMapping("/top10")
    @ApiOperation("销售Top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销售Top10,开始时间: {},结束时间: {}", begin, end);
        SalesTop10ReportVO reportVO = reportService.top10(begin, end);
        return Result.success(reportVO);
    }

    @GetMapping("/export")
    @ApiOperation("导出报表")
    public void exportReport(HttpServletResponse response) {
        reportService.exportBusinessDate(response);

    }

}