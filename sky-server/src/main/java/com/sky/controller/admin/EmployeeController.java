package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePasswordDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }



    /*
    修改密码
     */
    @PutMapping("/editPassword")
    public Result<String> editPassword(@RequestBody EmployeePasswordDTO employeePasswordDTO) {
        log.info("修改密码，员工ID：{}, 新密码：{}", employeePasswordDTO.getEmpId(), employeePasswordDTO.getNewPassword());
        employeeService.editPassword(employeePasswordDTO);
        return Result.success();
    }

    /*
    启用禁用员工账号
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, @RequestParam Long id) {
        log.info("修改员工状态，员工ID：{}，状态：{}", id, status);
        employeeService.updateStatus(id, status);
        return Result.success();
    }




    /*
    分页查询员工
     */
    @GetMapping("/page")
    public Result<PageResult> page(String name, Integer page, Integer pageSize) {
        PageResult pageResult = employeeService.page(name, page, pageSize);
        return Result.success(pageResult);
    }

    /*
    新增员工
     */
    @PostMapping
    public Result save(@RequestBody Employee employee){
        log.info("新增员工，员工数据：{}",employee);
        employeeService.save(employee);
        return Result.success();
    }

    /*
    根据id查询员工
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /*
    员工编辑
     */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee) {
        log.info("编辑员工，员工数据：{}", employee);
        employeeService.update(employee);
        return Result.success();
    }
}
