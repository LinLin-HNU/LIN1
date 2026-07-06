package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderStatisticsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.aspectj.weaver.ast.Or;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);

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
     * 历史订单查询
     * @return
     */
//    @Select("select * from orders where status=5 and user_id=#{userId}")
//    Page<Orders> historyOrders(Orders orders);

    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select * from orders where id=#{id}")
    Orders selectById(Long id);

    /**
     * 统计订单数量
     * @param status
     * @return
     */
    @Select("select count(id) from orders where status=#{status}")
    Integer countStatus(Integer status);

    /**
     * 根据订单状态和下单时间支付超时订单
     */
    @Select("select * from orders where status=#{status} and order_time<#{orderTime}")
    List<Orders> getPaymentTimeoutOrders(Integer status, LocalDateTime orderTime);

    /**
     * 根据日期统计当日营业额
     * SQL语句中的DATE(order_time)方法能够提取日期部分，与LocalDate精确匹配
     * sum()方法能顾求对应字段的总和
     * @param date
     * @return
     */
    @Select("select sum(amount) from orders where DATE(order_time)=#{date} and status=5")
    Double sumByDate(LocalDate date);

    /**
     * 统计指定日期的订单数量
     * @param date
     * @return
     */
    @Select("select count(id) from orders where DATE(order_time)=#{date}")
    Integer count(LocalDate date);

    /**
     * 统计指定日期的订单完成数量
     * @param date
     * @return
     */
    @Select("select count(id) from orders where DATE(orders.order_time)=#{date} and status=5")
    Integer countValid(LocalDate date);

    /**
     * 统计指定时间内销量前十
     * @param begin
     * @param end
     * @return
     */
    List<GoodsSalesDTO> selectTop10(LocalDateTime begin, LocalDateTime end);

    /**
     * 根据日期查询当日下单用户量
     */
    @Select("select count(user_id) from orders where order_time=#{date} and status=5")
    Integer selectUserCompleted(LocalDate date);

    /**
     * 查询订单数量
     * @return
     */
    @Select("select count(id) from orders")
    Integer countAll();



}


