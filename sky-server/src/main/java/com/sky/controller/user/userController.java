package com.sky.controller.user;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.userLoginService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.jsonwebtoken.impl.crypto.MacProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/user/user")
@RestController
@Api(tags = "用户登录管理")
@Slf4j
public class userController {

    @Autowired
    private userLoginService userLoginService;
    @Autowired
    private JwtProperties jwtProperties;


    /*前端发起请求，调用微信登录接口，获得code参数（通过code参数调用微信登录接口，获得openid和session_key）
     * 然后根据返回openid和session_key调用微信用户接口，获得用户信息（根据openid查询用户，判断用户是否存在）
     * 最后根据用户信息生成JWT Token（返回给前端）（code参数是登录的用户授权码）*/
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录，code={}", userLoginDTO.getCode());
        User user = userLoginService.login(userLoginDTO);

        // 生成JWT Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);

        return Result.success(new UserLoginVO(user.getId(), user.getOpenid(), token));
    }

}
