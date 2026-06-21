package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.SetmealDish;
import com.sky.mapper.MealMapper;
import com.sky.result.PageResult;
import com.sky.service.MealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealServiceImpl implements MealService {

    @Autowired
    private MealMapper mealMapper;

    @Override
    public void save(SetmealDTO meal) {

        //向套餐表插入数据
        mealMapper.insert(meal);
        //批量插入套餐菜品关系

        //获取生成的套餐id
        Long setmealId = meal.getId();

        List<SetmealDish> setmealDishes = meal.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        mealMapper.insertSetmealDish(setmealDishes);

    }

    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<SetmealVO> list = mealMapper.selectList(setmealPageQueryDTO);
        Page<SetmealVO> p = (Page<SetmealVO>) list;
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public void delete(List<Long> ids) {
        ids.forEach(id -> {
            // 删除套餐菜品关联
            mealMapper.deleteSetmealDish(id);
            // 删除套餐
            mealMapper.deleteById(id);
        });
    }

    @Override
    public SetmealVO getById(Long id) {
        return mealMapper.selectById(id);
    }

    @Override
    public void update(SetmealDTO meal) {
        mealMapper.update(meal);
        //更新套餐菜品关系：先删旧数据
        mealMapper.deleteSetmealDish(meal.getId());
        //再插入新数据
        List<SetmealDish> setmealDishes = meal.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            //前端不传 setmealId，需要手动设置
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(meal.getId());
            });
            mealMapper.insertSetmealDish(setmealDishes);
        }
    }

    @Override
    public void updateStatus(Integer status, List<Long> ids) {
        ids.forEach(id -> {
            mealMapper.updateStatus(id, status);
        });
    }
}