package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.anno.anno;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Override
    @anno(OperationType.INSERT)
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        /*向菜品表插入数据*/
        dishMapper.insertdish(dish);

        //获取菜品id
        Long dishId = dish.getId();

        //向菜品口味表插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && flavors.size() > 0) {
            //将菜品id赋值给口味
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishMapper.insertflavor(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        List<DishVO> list = dishMapper.SelectAll(dishPageQueryDTO);
        Page<DishVO> page = (Page<DishVO>) list;
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public DishVO getById(Long id) {
        //查询菜品基本信息
        Dish dish = dishMapper.SelectById(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        //查询菜品口味列表
        List<DishFlavor> flavors = dishMapper.selectFlavorsByDishId(id);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    @anno(OperationType.UPDATE)
    public void update(DishDTO dishDTO) {
        //更新菜品基本信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除旧口味
        List<Long> dishIdList = new java.util.ArrayList<>();
        dishIdList.add(dishDTO.getId());
        dishMapper.deletedishFlavorById(dishIdList);

        //插入新口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> flavor.setDishId(dishDTO.getId()));
            dishMapper.insertflavor(flavors);
        }
    }

    @Override
    public void deleteById(List<Long> id) {
        /*当前菜品是否是在售菜品*/
        for (Long dishId : id) {
            Dish dish = dishMapper.SelectByDishId(dishId);
            if (dish.getStatus() == 1) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        /*当前菜品是否关联了套餐*/
        List<Long> ids =  dishMapper.SelectDish(id);
        if(ids != null && ids.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        /*删除菜品*/
        dishMapper.deletedishById(id);

        /*删除菜品口味*/
        dishMapper.deletedishFlavorById(id);
    }

    @Override
    @anno(OperationType.UPDATE)
    public void startOrStop(Dish dish) {
        // 参数已改为 Dish 对象，直接使用
        dish.setStatus(dish.getStatus());
        dishMapper.update(dish);
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

}