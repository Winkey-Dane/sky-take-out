package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
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

    @Autowired
    private WorkspaceService workspaceService;

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

    /**
     * 导出营业数据报表
     * @param response
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        // 查询数据库获取营业数据---查询最近30天的数据
        LocalDate begin = LocalDate.now().minusDays(30); // 30天前
        LocalDate end = LocalDate.now().minusDays(1); // 昨天
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        // 使用POI将数据写入Excel文件
        // 从项目的类路径（classpath）中加载一个名为 运营数据报表模板.xlsx 的文件，
        // 并将其作为输入流（InputStream）对象返回。
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            // 获取第一个工作表
            XSSFSheet sheet1 = excel.getSheet("sheet1");
            // 填充数据
            sheet1.getRow(1).getCell(1).setCellValue("时间："+beginTime+"至"+endTime);
            XSSFRow row4 = sheet1.getRow(3); // 第四行
            row4.getCell(2).setCellValue(businessData.getTurnover()); // 营业额
            row4.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row4.getCell(6).setCellValue(businessData.getNewUsers());
            // 获得第五行
            XSSFRow row5 = sheet1.getRow(4);
            row5.getCell(2).setCellValue(businessData.getValidOrderCount()); // 有效订单数
            row5.getCell(4).setCellValue(businessData.getValidOrderCount());

            // 填充明细数据
            for (int i=0;i<30;i++){
                LocalDate date = begin.plusDays(i);
                // 查询某一天的营业数据
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                row4 = sheet1.getRow(7 + i); // 第八行开始填充数据
                row4.getCell(1).setCellValue(date.toString()); // 日期
                row4.getCell(2).setCellValue(businessData1.getTurnover()); // 营业额
                row4.getCell(3).setCellValue(businessData1.getValidOrderCount()); // 有效订单数
                row4.getCell(4).setCellValue(businessData1.getOrderCompletionRate()); // 订单完成率
                row4.getCell(5).setCellValue(businessData1.getUnitPrice()); // 平均客单价
                row4.getCell(6).setCellValue(businessData1.getNewUsers()); // 新增用户数
            }

            // 通过response将Excel文件写回客户端,弹出下载框
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);
            // 关闭资源
            outputStream.close();
            excel.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
