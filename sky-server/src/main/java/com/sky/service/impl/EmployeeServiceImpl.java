package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePasswordDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /*
     * 新增员工
     */
    @Override
    public void save(Employee employee) {
        //1.设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setStatus(StatusConstant.ENABLE);
        //2.设置创建时间、更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //3.设置创建人、更新人
//        employee.setCreateUser(employee.getUpdateUser());
//        employee.setUpdateUser(employee.getUpdateUser());

        employeeMapper.insert(employee);
    }

    @Override
    public PageResult page(String name, Integer page, Integer pageSize) {
        // 开启分页查询
        PageHelper.startPage(page, pageSize);

        // 执行查询
        List<Employee> employeeList = employeeMapper.list(name);

        // 封装分页结果
        Page<Employee> pageInfo = (Page<Employee>) employeeList;
        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }

    @Override
    public Employee getById(Long id) {
        return employeeMapper.getById(id);
    }

    @Override
    public void update(Employee employee) {
        employeeMapper.update(employee);
    }

    @Override
    public void editPassword(EmployeePasswordDTO employeePasswordDTO) {
        // 可选：校验旧密码（需先查询员工信息并比对）
        // Employee employee = employeeMapper.getById(employeePasswordDTO.getEmpId());
        // if (!passwordEncoder.matches(employeePasswordDTO.getOldPassword(), employee.getPassword())) {
        //     throw new RuntimeException("旧密码错误");
        // }

        // 更新密码（假设密码已由前端加密或在 service 中加密）
        Employee employee = new Employee();
        employee.setId(employeePasswordDTO.getEmpId());
        employee.setPassword(employeePasswordDTO.getNewPassword()); // 实际应使用 BCrypt 加密
//        employee.setUpdateTime(LocalDateTime.now()); // 修复：使用 now() 方法获取当前时间
//        employee.setUpdateUser(1L); // 实际应从上下文获取当前登录员工ID

        employeeMapper.updatePassword(employee);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(1L); // 实际应从上下文获取当前登录员工ID（如 ThreadLocal 或 JwtUtil）

        employeeMapper.updateStatus(employee);
    }

}
