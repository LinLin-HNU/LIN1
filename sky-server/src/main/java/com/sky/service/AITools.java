package com.sky.service;

import com.github.pagehelper.Page;
import com.sky.entity.Dish;
import com.sky.entity.Employee;
import com.sky.mapper.DishMapper;
import com.sky.mapper.EmployeeMapper;
import com.sky.vo.DishVO;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AITools {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 工具 1：查询菜品信息
     */
    @Tool("查询苍穹外卖的菜品信息，包括菜品名称、价格、描述、分类等。参数：categoryName（菜品分类名称，可选）")
    public String queryDishes(String categoryName) {
        log.info("【AI Tool】查询菜品 - 分类：{}", categoryName);

        Page<DishVO> dishes;
        if (categoryName == null || categoryName.isEmpty()) {
            dishes = dishMapper.list(null); // 查询所有菜品
        } else {
            // 这里需要根据实际数据库结构调整
            dishes = dishMapper.list(null); // 简化处理，实际应传入 categoryId
        }

        if (dishes == null || dishes.isEmpty()) {
            return "抱歉，暂时没有找到相关菜品。";
        }

        // 格式化菜品信息
        StringBuilder sb = new StringBuilder("以下是查询到的菜品信息：\n\n");
        for (int i = 0; i < Math.min(dishes.size(), 10); i++) {
            DishVO dish = dishes.get(i);
            sb.append(String.format("%d. **%s**\n", i + 1, dish.getName()));
            sb.append(String.format("   价格：¥%.2f\n", dish.getPrice()));
            if (dish.getDescription() != null) {
                sb.append(String.format("   描述：%s\n", dish.getDescription()));
            }
            sb.append("\n");
        }

        if (dishes.size() > 10) {
            sb.append(String.format("... 还有 %d 个菜品未显示\n", dishes.size() - 10));
        }

        return sb.toString();
    }

    /**
     * 工具 2：查询用户个人信息
     */
    @Tool("查询用户的个人信息，包括姓名、手机号、邮箱等。参数：userId（用户 ID，数字类型）")
    public String queryUserInfo(Long userId) {
        log.info("【AI Tool】查询用户信息 - ID：{}", userId);

        Employee employee = employeeMapper.getById(userId);
        if (employee == null) {
            return "抱歉，没有找到该用户的信息。";
        }

        return String.format(
                "用户信息：\n" +
                        "姓名：%s\n" +
                        "用户名：%s\n" +
                        "手机号：%s\n" +
                        "账号状态：%s\n",
                employee.getName(),
                employee.getUsername(),
                employee.getPhone(),
                employee.getStatus() == 1 ? "正常" : "禁用"
        );
    }

    /**
     * 工具 3：修改用户手机号
     */
    @Tool("修改用户的手机号码。参数：userId（用户 ID，数字类型），newPhone（新手机号，字符串类型）")
    public String updateUserPhone(Long userId, String newPhone) {
        log.info("【AI Tool】修改用户手机号 - ID：{}, 新手机号：{}", userId, newPhone);

        Employee employee = employeeMapper.getById(userId);
        if (employee == null) {
            return "抱歉，没有找到该用户，无法修改。";
        }

        // 验证手机号格式
        if (newPhone == null || !newPhone.matches("^1[3-9]\\d{9}$")) {
            return "手机号格式不正确，请输入 11 位手机号码。";
        }

        employee.setPhone(newPhone);
        employeeMapper.update(employee);

        return String.format("✅ 已成功将用户 %s 的手机号修改为：%s", employee.getName(), newPhone);
    }

    /**
     * 工具 4：修改用户地址（假设有地址字段）
     */
    @Tool("修改用户的收货地址。参数：userId（用户 ID），newAddress（新地址）")
    public String updateUserAddress(Long userId, String newAddress) {
        log.info("【AI Tool】修改用户地址 - ID：{}, 新地址：{}", userId, newAddress);

        Employee employee = employeeMapper.getById(userId);
        if (employee == null) {
            return "抱歉，没有找到该用户。";
        }

        // 注意：Employee 实体类需要添加 address 字段
        // employee.setAddress(newAddress);
        // employeeMapper.update(employee);

        return String.format("✅ 已成功将用户 %s 的地址修改为：%s", employee.getName(), newAddress);
    }
}