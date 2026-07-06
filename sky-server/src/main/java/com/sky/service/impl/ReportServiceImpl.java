package com.sky.service.impl;

import com.ctc.wstx.util.StringUtil;
import com.sky.dto.GoodsSalesDTO;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        //统计日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        //plusDays()：日期类型向后推迟
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //调用StringUtils工具类中的join方法，把集合转为字符串，并用，分割每个集合元素
        StringUtils.join(dateList, ",");

        //统计营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Double turnover = orderMapper.sumByDate(date);
            turnoverList.add(turnover == null ? 0.0 : turnover);//当日无营业额，就转为空
            log.info("营业额：{}", turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    /**
     * 用户统计
     * 统计每日用户新增数量和总量
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        //统计日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        //plusDays()：日期类型向后推迟
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //调用StringUtils工具类中的join方法，把集合转为字符串，并用，分割每个集合元素
        StringUtils.join(dateList, ",");

        //统计用户新增数量
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer newUser = userMapper.countUserByDate(date);
            newUserList.add(newUser);
            log.info("用户新增数量：{}", newUser);
        }

        //统计用户总量
        List<Integer> totalUserList = new ArrayList<>();
        int sum=0;
        for (Integer i : newUserList) {
            sum+=i;
            totalUserList.add(sum);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .build();

    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersStatistics(LocalDate begin, LocalDate end) {
        //统计日期
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        //plusDays()：日期类型向后推迟
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        //调用StringUtils工具类中的join方法，把集合转为字符串，并用，分割每个集合元素
        StringUtils.join(dateList, ",");

        //统计每日订单量和每日有效订单量
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Integer orderCount = orderMapper.count(date);
            orderCountList.add(orderCount);
            Integer validOrderCount = orderMapper.countValid(date);
            validOrderCountList.add(validOrderCount);
            log.info("订单量：{}", orderCount);
        }

        //统计订单总数和有效订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //统计订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }

    /**
     * 查询销量排名前十
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
//        List<LocalDate> dateList = new ArrayList<>();
//        dateList.add(begin);
//        while(!begin.equals(end)){
//           begin= begin.plusDays(1);
//           dateList.add(begin);
//        }

        //LocalDate类型转换为LocalDateTime
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        //查询给定日期中订单中的所有
        List<GoodsSalesDTO> goodsSalesDTOS = orderMapper.selectTop10(beginTime, endTime);
        List<String> nameList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();
        for (GoodsSalesDTO goodsSalesDTO : goodsSalesDTOS) {
            nameList.add(goodsSalesDTO.getName());
            numList.add(goodsSalesDTO.getNumber());
        }
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numList,","))
                .build();


    }

    /**
     * 导出Excel数据
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        //1.查询数据库，获取营业数据
        LocalDate beginDate = LocalDate.now().minusDays(30);//获取当前时间减去30天
        LocalDate endDate = LocalDate.now().minusDays(1);

//        LocalDateTime beginTime = LocalDateTime.of(beginDate, LocalTime.MIN);
//        LocalDateTime endTime = LocalDateTime.of(endDate, LocalTime.MAX);

        //这里原本是要查今30天的数据，但我的方法需要大改（大多都是只能查当日的），所以先暂定求昨天的
        BusinessDataVO businessDataVO = workSpaceService.businessData(endDate);


        //2.通过POI将数据写入Excel
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        //基于模板文件创造一个Excel文件
        try {
            //获得工作簿对象，便于后续对工作簿进行操作
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);
            //获得表格标签的sheet标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //设置表格第二行第二列时间
            sheet.getRow(1).getCell(1).setCellValue("时间："+endDate);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for(int i = 0 ;i < 30; i++){
                LocalDate date = beginDate.plusDays(i);
                BusinessDataVO businessDataVO1 = workSpaceService.businessData(date);

                //获得某一行
                row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataVO1.getTurnover());
                row.getCell(3).setCellValue(businessDataVO1.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataVO1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataVO1.getUnitPrice());
                row.getCell(6).setCellValue(businessDataVO1.getNewUsers());
            }



            //3.通过输出流将Excel下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);


            //4.关闭资源
            excel.close();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }





    }
}
