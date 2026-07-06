package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 查询今日运营数据
     * @return
     */
    @Override
    public BusinessDataVO businessData(LocalDate date) {
        //新增用户数
        Integer newUsers = userMapper.countUserByDate(date);
        if(newUsers == null)
            newUsers = 0;

        Integer orderNums = orderMapper.count(date);
        //有效订单数
        Integer validOrderNums = orderMapper.countValid(date);
        if(validOrderNums == null){
            validOrderNums = 0;
        }
        //统计订单完成率
        Double rate;
        if(orderNums!=null && orderNums > 0){
            rate = validOrderNums.doubleValue() / orderNums;//.doubleValue()// 转换为浮点数
        }else {
             rate = 0.0;
        }
        //统计营业额
        Double sumByDate = orderMapper.sumByDate(date);
        if(sumByDate == null)
            sumByDate = 0.0;
        //统计客单价
        Integer userCompleted = orderMapper.selectUserCompleted(date);
        Double averValue;
        if(userCompleted != null && userCompleted > 0){
            averValue = sumByDate.doubleValue()/ userCompleted;
        }else {
            averValue = 0.0;
        }
//      优化建议：
//        Double sumByDate = Objects.requireNonNullElse(orderMapper.sumByDate(LocalDate.now()), 0.0);
//        Integer userCompleted = Objects.requireNonNullElse(orderMapper.selectUserCompleted(LocalDate.now()), 0);
//
//        Double averValue = (userCompleted > 0)
//                ? sumByDate / userCompleted.doubleValue()
//                : 0.0;

        return BusinessDataVO.builder()
                .newUsers(newUsers)
                .validOrderCount(validOrderNums)
                .orderCompletionRate(rate)
                .turnover(sumByDate)
                .unitPrice(averValue)
                .build();
    }


    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO setmealOver() {
        //查询起售停售数量
        Integer stopStatus = setmealMapper.selectByStatus(0);//停售0 起售1
        Integer startStatus = setmealMapper.selectByStatus(1);

        return SetmealOverViewVO.builder()
                .discontinued(stopStatus)
                .sold(startStatus)
                .build();
    }

    /**
     * 查询菜品总览
     * @return
     */
    @Override
    public DishOverViewVO dishOver() {
        Integer stopStatus = dishMapper.countByStatus(0);
        Integer startStatus = dishMapper.countByStatus(1);

        return DishOverViewVO.builder()
                .discontinued(stopStatus)
                .sold(startStatus)
                .build();
    }

    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO orderOver() {
        //查询全部订单
        Integer countAll = orderMapper.countAll();
        if(countAll==null)
            countAll = 0;
        //已取消数量
        Integer cancelNum = orderMapper.countStatus(Orders.CANCELLED);
        if(cancelNum==null)
            cancelNum = 0;
        //已完成数量
        Integer completedNum = orderMapper.countStatus(Orders.COMPLETED);
        if(completedNum==null)
            completedNum = 0;
        //带派送数量
        Integer waitDeliver = orderMapper.countStatus(Orders.CONFIRMED);
        if(waitDeliver==null)
            waitDeliver = 0;
        //待接单数量
        Integer waitReceive = orderMapper.countStatus(Orders.REFUND);
        if(waitReceive==null)
            waitReceive = 0;

        return OrderOverViewVO.builder()
                .allOrders(countAll)
                .cancelledOrders(cancelNum)
                .completedOrders(completedNum)
                .deliveredOrders(waitDeliver)
                .waitingOrders(waitReceive)
                .build();

    }




}
