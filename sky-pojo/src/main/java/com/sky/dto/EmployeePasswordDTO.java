package com.sky.dto;

import lombok.Data;

@Data
public class EmployeePasswordDTO {
    private Long empId;        // 员工ID（对应接口中 empId）
    private String newPassword; // 新密码
    private String oldPassword; // 旧密码（可选用于校验，但接口未强制要求校验逻辑）
}

