package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
/*dto是服务器端传输数据的实体类，vo是往前端传输数据的实体类*/
/*DTO是数据转对象 VO是显示对象信息 VO才是返回前端的实体类*/
@ApiModel(description = "员工登录时传递的数据模型")
public class EmployeeLoginDTO implements Serializable {

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

}
