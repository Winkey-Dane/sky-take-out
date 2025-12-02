package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单表数据
     * @param order
     */
    void insert(Orders order);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 历史订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据id查询订单详情
     * @param id
     * @return
     */
    @Select("select * from orders where id = #{id}")
    Orders getByOrderId(Integer id);

    /**
     * 统计订单状态数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间小于某个时间查询订单列表
     * @param status
     * @param orderTime
     * @return
     */
    @Select("select * from orders where status = #{status} and order_time < #{orderTime}")
    List<Orders> getByStatusAndTimeLT(int status, LocalDateTime orderTime);

    /**
     * 根据动态条件获取当天营业额
     * @param map
     * @return
     */
    Double getTurnoverByDate(Map map);

    /**
     * 根据动态条件获取截止到今天的订单总数
     * @param map
     * @return
     */
    Integer getOrderCountByToday(Map map);

    /**
     * 根据动态条件获取截止到今天的完成订单总数
     * @param map
     * @return
     */
    Integer getOrderCompletedCountByToday(Map map);

    /**
     * 根据动态条件获取某天的订单总数
     * @param map
     * @return
     */
    Integer getOrderCountByDate(Map map);

    /**
     * 根据动态条件获取某天的完成订单总数
     * @param map
     * @return
     */
    Integer getOrderCompletedCountByDate(Map map);
}
