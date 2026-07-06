package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@Api(tags = "数据统计")
@Slf4j
@RequestMapping("/admin/report")
public class ReportController {


    @Autowired
    private ReportService reportService;

    /**
     * 统计营业额
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("统计营业额")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("统计营业额：{}，{}",begin,end);
        return Result.success(reportService.turnoverStatistics(begin,end));
    }

    /**
     * 用户统计
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                               @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("用户统计：{}，{}",begin,end);
        return Result.success(reportService.userStatistics(begin,end));
    }

    /**
     * 订单统计
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> ordersStatistics(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("订单统计：{}，{}",begin,end);
        return Result.success(reportService.ordersStatistics(begin,end));
    }

    /**
     * 查询销量排名TOP10
     * @DateTimeFormat(pattern = "yyyy-MM-dd")中，MM是月份，mm是分钟
     */
    @GetMapping("/top10")
    @ApiOperation("销量前十")
    public Result<SalesTop10ReportVO> salesTop10(@DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                  @DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end){
        log.info("查询销量排名TOP10：{}，{}",begin,end);
        return Result.success(reportService.salesTop10(begin,end));
    }

    /**
     * Excel导出
     * 正确理解：它不是“传入参数”，而是“注入的响应通道”
     * HttpServletResponse response 是 Spring 自动注入的一个对象，代表“即将发回给浏览器的那个 HTTP 响应”。
     * 你通过它可以直接往响应里写 Excel 二进制数据，而不是返回 Java 对象
     */
    @GetMapping("/export")
    @ApiOperation("导出")
    public void export(HttpServletResponse response){
        log.info("导出数据");
        reportService.export(response);
    }
}
