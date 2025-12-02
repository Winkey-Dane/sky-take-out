package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    /**
     * 获取营业额统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate);

    /**
     * 获取用户数据统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate);

    /**
     * 获取订单数据统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    OrderReportVO getOrderStatistics(LocalDate beginDate, LocalDate endDate);
}
