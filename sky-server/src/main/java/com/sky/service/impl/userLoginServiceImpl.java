package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.userLoginService;
import com.sky.utils.HttpClientUtil;
import io.swagger.annotations.Api;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.LoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Api(tags = "微信登录")
public class userLoginServiceImpl implements userLoginService  {
    /*
    code
    仅用来交换 access_token、openid，仅此一个作用，无其他业务价值。
    openid
    业务核心标识：
    - 判断用户是否注册
    - 绑定账号、存储用户信息
    - 后续所有接口操作用户数据都靠openid
    */

    /*
    code
    临时授权票据，中间过渡凭证，不是用户标识。
    openid
    用户永久唯一身份ID，代表这个微信用户。
    */
    @Autowired
    private UserMapper userMapper;

    /*微信登录接口地址*/
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @SneakyThrows
    @Override
    public User login(UserLoginDTO userLoginDTO)  {
        log.info("userLoginDTO = {}", userLoginDTO);

        //调用微信官方登录接口,获得用户的openid和session_key(request(返回值(是JSON格式的字符串))里面就包含了openid和session_key)
        Map<String,String> map = new HashMap<>();
        /*应用ID*/
        map.put("appid", weChatProperties.getAppid());
        /*应用密钥*/
        map.put("secret", weChatProperties.getAppSecret());
        /*登录凭证（code）*/
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String result = HttpClientUtil.doGet(WX_LOGIN_URL, map);

        //解析result,获得openid（openid是用户的唯一标识，用于后续的用户操作）
        log.info("微信接口返回：{}", result);
        JSONObject jsonObject = JSON.parseObject(result);
        String openid = jsonObject.getString("openid");

        /*判断openid是否为空*/
        if(openid == null || openid.isEmpty()){
            throw new LoginException("调用微信微信登录接口失败：" + result);
        }

        //根据openid查询用户，判断用户是否存在
        User user = userMapper.selectByOpenid(openid);
        //如果用户不存在,则创建用户
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            //将用户插入到数据库
            userMapper.insert(user);
        }
        /*返回user对象*/
        return user;

    }
}
