package com.sky.controller.admin;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDate;


@RestController
@Api("后台报表相关接口")
@RequestMapping("/admin/report")
@Slf4j
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计报表")
    public Result<TurnoverReportVO> turnoverStatistics(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("获取营业额统计报表，beginDate：{}，endDate：{}", beginDate, endDate);
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverStatistics(beginDate, endDate);
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计报表")
    public Result<UserReportVO> userStatisticsReport(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("获取用户统计报表，beginDate：{}，endDate：{}", beginDate, endDate);
        return  Result.success(reportService.getUserStatistics(beginDate, endDate));
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计报表")
    public Result<OrderReportVO> uordersStatisticsReport(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("获取用户统计报表，beginDate：{}，endDate：{}", beginDate, endDate);
        return Result.success(reportService.getOrderStatistics(beginDate, endDate));
    }

    /**
     * 销量前十统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    @GetMapping("/top10")
    @ApiOperation("销量前十统计报表")
    public Result<SalesTop10ReportVO> salesTop10Report(
            @RequestParam("begin") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate beginDate,
            @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        log.info("获取销量前十统计报表，beginDate：{}，endDate：{}", beginDate, endDate);
        return Result.success(reportService.getSalesTop10Report(beginDate, endDate));
    }
}
