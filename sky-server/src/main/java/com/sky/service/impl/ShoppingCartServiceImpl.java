package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.MealMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.DishService;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private MealMapper mealMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public void save(ShoppingCartDTO shoppingCartDTO) {

        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        String dishFlavor = shoppingCartDTO.getDishFlavor();
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();

        /*判断购物车里面是否有菜品*/
        if(dishId != null && dishId != 0){
            /*判断购物车是否存在该菜品，如果存在，只需将数量加一*/
            ShoppingCart ById = shoppingCartMapper.SelectByDishId(dishId);
            if(ById != null){
                ById.setNumber(ById.getNumber() + 1);
                shoppingCartMapper.updateById(ById);
                return;
            }

            /*如果不存在，就新增*/
            shoppingCart.setDishId(dishId);
            Dish byId = dishMapper.SelectById(dishId);
            shoppingCart.setImage(byId.getImage());
            shoppingCart.setAmount(byId.getPrice());
            shoppingCart.setName(byId.getName());
            if(dishFlavor != null && !dishFlavor.isEmpty()){
                shoppingCart.setDishFlavor(dishFlavor);
            }
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(userId);
            shoppingCartMapper.insert(shoppingCart);
        }

        /*判断购物车里面是否有套餐*/
        if(setmealId != null && setmealId != 0){
            /*判断购物车是否存在该套餐，如果存在，只需将数量加一*/
            ShoppingCart ById = shoppingCartMapper.SelectBySetmealId(setmealId);
            if(ById != null){
                ById.setNumber(ById.getNumber() + 1);
                shoppingCartMapper.updateById(ById);
                return;
            }
            /*如果不存在，就新增*/
            shoppingCart.setSetmealId(setmealId);
            SetmealVO byId = mealMapper.selectById(setmealId);
            shoppingCart.setImage(byId.getImage());
            shoppingCart.setAmount(byId.getPrice());
            shoppingCart.setName(byId.getName());
            shoppingCart.setNumber(1);
            shoppingCart.setUserId(userId);
            shoppingCartMapper.insert(shoppingCart);
        }
    }

    @Override
    public List<ShoppingCart> list() {
        return shoppingCartMapper.SelectByUserId(BaseContext.getCurrentId());
    }

    @Override
    public void clean() {
        shoppingCartMapper.clean();
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        Long dishId = shoppingCartDTO.getDishId();
        Long setmealId = shoppingCartDTO.getSetmealId();
        ShoppingCart shoppingCart = new ShoppingCart();
        Long userId = BaseContext.getCurrentId();
        if(dishId != null && dishId != 0){
            shoppingCart = shoppingCartMapper.SelectByDishId(dishId);
        }
        if(setmealId != null && setmealId != 0){
            shoppingCart = shoppingCartMapper.SelectBySetmealId(setmealId);
        }
        if(shoppingCart == null){
            return;
        }
        if(shoppingCart.getNumber() == 1){
            shoppingCartMapper.deleteById(shoppingCart.getId());
        }else{
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateById(shoppingCart);
        }
    }
}
