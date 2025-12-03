package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 获取营业额统计报表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // 当前集合用于存放从beginDate到endDate的每天日期
        List<LocalDate> dateList = everyDay(beginDate, endDate);
        String date = StringUtils.join(dateList, ",");
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(date);

        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            // 营业额为 装态为已完成的订单总金额之和
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.getTurnoverByDate(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ","));
        return turnoverReportVO;
    }

    /**
     * 获取用户数据统计报表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> dateList = everyDay(beginDate, endDate);

        String date = StringUtils.join(dateList, ",");
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(date);

        List<Integer> userTotalList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime", endTime);
            Integer userTotal = userMapper.getUserTotalByDate(map);
            map.put("beginTime", beginTime);
            Integer newUserTotal = userMapper.getNewUserTotalByDate(map);
            newUserTotal = newUserTotal == null ? 0 : newUserTotal;
            userTotal = userTotal == null ? 0 : userTotal;
            newUserList.add(newUserTotal);
            userTotalList.add(userTotal);
        }
        String join = StringUtils.join(userTotalList, ",");
        String newJoin = StringUtils.join(newUserList, ",");
        userReportVO.setTotalUserList(join);
        userReportVO.setNewUserList(newJoin);
        return userReportVO;
    }

    /**
     * 获取订单数据统计报表
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate beginDate, LocalDate endDate) {
        List<LocalDate> dateList = everyDay(beginDate, endDate);
        String date = StringUtils.join(dateList, ",");
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(date);

        // 查询dateList中每一天的订单数量和订单完成数量
        List<Integer> orderTotalListEveList = new ArrayList<>(); // 每日订单总数集合
        List<Integer> orderCompletedListEveList = new ArrayList<>(); // 每日订单完成数量集合
        Integer orderTotalSoFar = 0; // 截至目前为止订单总数量
        Integer orderCompletedSoFar = 0; // 截至目前为止订单完成数量
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            Map map = new HashMap();
            map.put("endTime", endTime);
            orderTotalSoFar = orderMapper.getOrderCountByToday(map); // 截至目前为止订单总数量
            map.put("status", Orders.COMPLETED);
            orderCompletedSoFar = orderMapper.getOrderCompletedCountByToday(map); // 截至目前为止订单完成数量
            map.put("beginTime", beginTime);
            Integer orderTotalEveryDay = orderMapper.getOrderCountByDate(map); // 每日订单总数
            orderTotalListEveList.add(orderTotalEveryDay);
            Integer orderCompletedEveryDay = orderMapper.getOrderCompletedCountByDate(map); // 每日订单完成数量
            orderCompletedListEveList.add(orderCompletedEveryDay);
        }
        orderReportVO.setOrderCountList(StringUtils.join(orderTotalListEveList, ","));
        orderReportVO.setValidOrderCountList(StringUtils.join(orderCompletedListEveList, ","));
        orderReportVO.setValidOrderCount(orderCompletedSoFar);
        orderReportVO.setTotalOrderCount(orderTotalSoFar);
        Double orderCompletionRate = orderCompletedSoFar.doubleValue() / orderTotalSoFar.doubleValue();
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    /**
     * 获取销售排行榜前十报表
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Report(LocalDate beginDate, LocalDate endDate) {
        LocalDateTime beginTime = LocalDateTime.of(beginDate, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        List<String> nameList = new ArrayList<>();
        List<String> numberList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : salesTop10) {
            nameList.add(goodsSalesDTO.getName());
            numberList.add(String.valueOf(goodsSalesDTO.getNumber()));
        }
        String nameL = StringUtils.join(nameList, ",");
        String numberL = StringUtils.join(numberList, ",");
        salesTop10ReportVO.setNameList(nameL);
        salesTop10ReportVO.setNumberList(numberL);
        return salesTop10ReportVO;
    }

    private List<LocalDate> everyDay(LocalDate beginDate, LocalDate endDate) {
        // 当前集合用于存放从beginDate到endDate的每天日期
        List<LocalDate> dateList = new ArrayList();
        dateList.add(beginDate);
        while (!beginDate.equals(endDate)) {
            beginDate = beginDate.plusDays(1);  // 更新beginDate
            dateList.add(beginDate);
        }
        return dateList;
    }
}
