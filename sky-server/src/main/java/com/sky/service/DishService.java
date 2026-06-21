package com.sky.service;

import com.sky.anno.anno;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    public void save(DishDTO dishDTO);
    public PageResult page(DishPageQueryDTO dishPageQueryDTO);
    public DishVO getById(Long id);

    public void update(DishDTO dishDTO);
    public void deleteById(List<Long> id);

    public void startOrStop(Dish dish);
    public List<Dish> list(Long categoryId);
}