package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    ShoppingCart SelectByDishId(Long dishId);
    ShoppingCart SelectBySetmealId(Long setmealId);
    void updateById(ShoppingCart shoppingCart);
    void insert(ShoppingCart shoppingCart);
    List<ShoppingCart> SelectByUserId(Long userId);
    void deleteById(Long id);
    void deleteByUserId(Long userId);
    void clean();
}
