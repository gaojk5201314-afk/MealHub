package com.sky.mapper;

import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    TurnoverReportVO Statistics(LocalDateTime begin, LocalDateTime end);
    String StatisticsUser(LocalDate date);
    String StatisticsNewUser(LocalDateTime begin, LocalDateTime end);

    Integer OrderStatistic(LocalDateTime begin, LocalDateTime end);
    String validOrderStatistic(LocalDateTime begin, LocalDateTime end);
    String DailyOrderStatistic(LocalDateTime begin, LocalDateTime end);
    Integer ValidOrder(LocalDateTime begin, LocalDateTime end);
    List<Map<String, Object>> Top10(LocalDateTime begin, LocalDateTime end);
    Double sumByMap(Map map);
    Integer countByMap(Map map);
}