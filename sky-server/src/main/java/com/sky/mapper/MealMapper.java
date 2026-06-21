package com.sky.mapper;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.SetmealDish;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MealMapper {
    void insert(SetmealDTO meal);
    List<SetmealVO> selectList(SetmealPageQueryDTO setmealPageQueryDTO);
    void deleteById(Long ids);
    SetmealVO selectById(Long id);
    void update(SetmealDTO meal);
    //删除套餐菜品关系
    void deleteSetmealDish(Long setmealId);
    //插入套餐菜品关系
    void insertSetmealDish(List<SetmealDish> setmealDishes);
    //更新套餐状态
    void updateStatus(Long id, Integer status);
    //更新套餐菜品状态
    void updateDishStatus(List<Long> ids, Integer status);
}
