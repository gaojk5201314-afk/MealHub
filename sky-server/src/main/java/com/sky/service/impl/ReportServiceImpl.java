package com.sky.service.impl;

import com.sky.mapper.ReportMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.LocalTime.MAX;
import static java.time.LocalTime.MIN;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private ReportMapper reportMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        List<String> dateList = new ArrayList<>();
        List<String> turnoverList = new ArrayList<>();

        // 遍历从begin到end的所有日期（包括end）
        for (LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            dateList.add(date.format(FORMATTER));

            LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);

            // 查询当日营业额
            TurnoverReportVO result = reportMapper.Statistics(localDateTime, endDateTime);
            if (result != null && result.getTurnoverList() != null) {
                turnoverList.add(result.getTurnoverList());
            } else {
                turnoverList.add("0");
            }
        }

        turnoverReportVO.setDateList(String.join(",", dateList));
        turnoverReportVO.setTurnoverList(String.join(",", turnoverList));
        return turnoverReportVO;
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        UserReportVO userReportVO = new UserReportVO();
        List<String> dateList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();
        for (LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)) {
            dateList.add(date.format(FORMATTER));
            LocalDateTime localDateTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endDateTime = LocalDateTime.of(date, LocalTime.MAX);
            // 查询当日用户总量
            String result = reportMapper.StatisticsUser(date);
            if (result != null && !result.isEmpty()) {
                totalUserList.add(result);
            } else {
                totalUserList.add("0");
            }
            // 查询当日新增用户
            String resultNew = reportMapper.StatisticsNewUser(localDateTime, endDateTime);
            if (resultNew != null && !resultNew.isEmpty()) {
                newUserList.add(resultNew);
            } else {
                newUserList.add("0");
            }
        }
        userReportVO.setDateList(String.join(",", dateList));
        userReportVO.setTotalUserList(String.join(",", totalUserList));
        userReportVO.setNewUserList(String.join(",", newUserList));
        return userReportVO;
    }

    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        //日期
        List<String> dateList = new ArrayList<>();
        //每日订单数
        List<String> orderCountList = new ArrayList<>();
        //每日有效订单数
        List<String> validOrderCountList = new ArrayList<>();
        //订单总数
        Integer totalOrderCount = 0;
        //有效订单数
        Integer validOrderCount = 0;
        //订单完成率
        Double orderCompletionRate;

        for(LocalDate date = begin; date.isBefore(end.plusDays(1)); date = date.plusDays(1)){
            dateList.add(date.format(FORMATTER));
            //LocalDateTime 类型（精确到时分秒）
            LocalDateTime localDateTimeBegin = LocalDateTime.of(date, MIN);
            LocalDateTime localDateTimeEnd = LocalDateTime.of(date, MAX);

            String OrderResult = reportMapper.DailyOrderStatistic(localDateTimeBegin,localDateTimeEnd);
            if(OrderResult != null && !OrderResult.isEmpty()){
                orderCountList.add(OrderResult);
            }else{
                orderCountList.add("0");
            }

            String result = reportMapper.validOrderStatistic(localDateTimeBegin,localDateTimeEnd);
            if(result != null && !result.isEmpty()){
                validOrderCountList.add(result);
            }else{
                validOrderCountList.add("0");
            }

        }

        // 将 LocalDate 转换为 LocalDateTime，确保时间范围一致
        // 因为 LocalDate 只包含日期信息，而 LocalDateTime 包含日期和时间信息，所以需要将 LocalDate 转换为 LocalDateTime 类型
        // 并且sql语句中比较的是order_time字段，是LocalDateTime类型，所以需要将begin和end转换为LocalDateTime类型
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);

        Integer totalOrderCountResult = reportMapper.OrderStatistic(beginDateTime,endDateTime);
        Integer validOrderCountResult = reportMapper.ValidOrder(beginDateTime,endDateTime);

        if(validOrderCountResult != null && totalOrderCountResult != null && totalOrderCountResult != 0){
            orderCompletionRate = validOrderCountResult.doubleValue() / totalOrderCountResult.doubleValue();
        }else{
            orderCompletionRate = 0.0;
        }

        orderReportVO.setDateList(String.join(",", dateList));
        orderReportVO.setOrderCountList(String.join(",", orderCountList));
        orderReportVO.setValidOrderCountList(String.join(",", validOrderCountList));
        orderReportVO.setTotalOrderCount(totalOrderCountResult);
        orderReportVO.setValidOrderCount(validOrderCountResult);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO top10(LocalDate begin, LocalDate end) {
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        //查询Top10商品
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);
        /*
        注意：o.status = 5 放在了 LEFT JOIN 的 ON 子句中，而不是 WHERE 子句中。
        ON 子句：只影响连接操作，决定右表（orders）哪些行参与连接，但左表（order_detail）的所有行仍会被保留，不满足的只是右表字段显示为 NULL
        WHERE 子句：真正过滤最终结果集
        */
        List<Map<String, Object>> listResult = reportMapper.Top10(beginDateTime, endDateTime);
        if (listResult != null && !listResult.isEmpty()) {
            for (Map<String, Object> item : listResult) {
                String name = (String) item.get("name");
                BigDecimal number = (BigDecimal) item.get("number");
                nameList.add(name);
                numberList.add(number.toString());
            }
        }
        salesTop10ReportVO.setNameList(String.join(",", nameList));
        salesTop10ReportVO.setNumberList(String.join(",", numberList));
        return salesTop10ReportVO;
    }

    @Override
    public void exportBusinessDate(HttpServletResponse response) {
        //获取开始时间结束时间
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDateTime beginDateTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询营业数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(beginDateTime, endDateTime);
        //获取模版文件流
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(resourceAsStream);
            //填充数据到模版
            //获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            //获取第2行
            Row row = sheet.getRow(1);
            //获取第2列
            Cell cell = row.getCell(1);
            //设置值
            cell.setCellValue("统计时间为：" + begin + "至" + end);
            Row row1 = sheet.getRow(3);
            row1.getCell(2).setCellValue(businessDataVO.getTurnover());
            row1.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row1.getCell(6).setCellValue(businessDataVO.getNewUsers());

            Row row2 = sheet.getRow(4);
            row2.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row2.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for(LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)){
                Row row3 = sheet.getRow((int) (7+(date.toEpochDay()-begin.toEpochDay())));
                LocalDateTime localDateTimeBegin = LocalDateTime.of(date, LocalTime.MIN);
                LocalDateTime localDateTimeEnd = LocalDateTime.of(date, LocalTime.MAX);
                //设置日期
                row3.getCell(1).setCellValue(String.valueOf(date));
                BusinessDataVO businessDataVO1 = workspaceService.getBusinessData(localDateTimeBegin, localDateTimeEnd);

                row3.getCell(2).setCellValue(businessDataVO1.getTurnover());
                row3.getCell(3).setCellValue(businessDataVO1.getValidOrderCount());
                row3.getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());
                row3.getCell(5).setCellValue(businessDataVO1.getUnitPrice());
                row3.getCell(6).setCellValue(businessDataVO1.getNewUsers());
            }

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);

            outputStream.close();
            workbook.close();
            resourceAsStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}