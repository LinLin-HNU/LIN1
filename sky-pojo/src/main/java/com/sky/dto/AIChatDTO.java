package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "AI对话请求")
public class AIChatDTO {

    @ApiModelProperty(value = "用户消息", required = true)
    private String message;
}
