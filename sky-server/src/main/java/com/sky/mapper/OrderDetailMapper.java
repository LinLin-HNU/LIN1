package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单详情数据
     * @param orderDetailList
     */
    void insertBatch(List<OrderDetail> orderDetailList);

    /**
     * 根据订单号查询该订单下的所有订单详情
     * @param ordersId
     * @return
     */
    @Select("select * from order_detail where order_id=#{ordersId}")
    List<OrderDetail> getByOrderId(Long ordersId);

    /**
     * 根据订单id查询所有的菜品名称
     * @param id
     * @return
     */
    @Select("select name from order_detail where order_id=#{id}")
    List<String> getOrderDishes(Long id);










}
