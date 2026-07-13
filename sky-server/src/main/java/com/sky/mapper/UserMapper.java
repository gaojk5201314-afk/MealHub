package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    //根据openid查询用户
    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);
    //插入用户
    int insert(User user);
    //根据用户id查询用户
    User selectById(Long id);
    Integer countByMap(Map map);
}
