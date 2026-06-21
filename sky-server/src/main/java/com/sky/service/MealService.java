package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface MealService {
    void save(SetmealDTO meal);
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);
    void delete(List<Long> ids);
    SetmealVO getById(Long id);
    void update(SetmealDTO meal);
    void updateStatus(Integer status, List<Long> ids);
}