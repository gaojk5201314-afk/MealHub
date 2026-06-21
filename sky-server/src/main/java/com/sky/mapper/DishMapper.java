package com.sky.mapper;

import com.sky.anno.anno;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
    void insertdish(Dish dish);

    void insertflavor(List<DishFlavor> flavors);

    List<DishVO> SelectAll(DishPageQueryDTO dishDTO);
    Dish SelectById(Long id);

    void update(Dish dish);
    void deletedishById(List<Long> ids);
    List<Long> SelectDish(List<Long> ids);
    Dish SelectByDishId(Long dishId);
    void deletedishFlavorById(List<Long> ids);
    List<Dish> list(Dish dish);

    /**
     * 根据菜品id查询口味列表
     * @param dishId
     * @return
     */
    List<DishFlavor> selectFlavorsByDishId(Long dishId);

}
