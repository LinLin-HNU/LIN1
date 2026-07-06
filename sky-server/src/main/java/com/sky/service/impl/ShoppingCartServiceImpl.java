package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //1.判断商品是否在购物车中
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
         cart.setUserId(BaseContext.getCurrentId());

        List<ShoppingCart> list = shoppingCartMapper.list(cart);

        //2.如果在购物车，只需数量加1
        if(list!=null && list.size()>0){
            //只可能查到一条数据，所以获取list集合的第一条数据即可
            ShoppingCart shoppingCart = list.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber()+1);
            //如果能够插上来，一定会得到其userId和Id
            shoppingCartMapper.updateNumber(shoppingCart);
        }else{
        //3.如果不在购物车，则需插入到购物车中
            Long setmealId = cart.getSetmealId();
            Long dishId = cart.getDishId();
            if(dishId!=null){
                DishVO dishVO = dishMapper.selectById(cart.getDishId());
                cart.setImage(dishVO.getImage());
                cart.setName(dishVO.getName());
                cart.setAmount(dishVO.getPrice());
                cart.setNumber(1);
                cart.setCreateTime(LocalDateTime.now());
            }else{
                Setmeal setmeal = setmealMapper.selectById(cart.getSetmealId());
                cart.setImage(setmeal.getImage());
                cart.setName(setmeal.getName());
                cart.setAmount(setmeal.getPrice());
                cart.setNumber(1);
                cart.setCreateTime(LocalDateTime.now());
            }
            shoppingCartMapper.insert(cart);
        }



    }

    /**
     * 获取当前用户的购物车数据
     * @return
     */
    @Override
    public List<ShoppingCart> list() {
        Long currentId = BaseContext.getCurrentId();
        return (shoppingCartMapper.selectByUserId(currentId));
    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long currentId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(currentId);
    }

    /**
     * 减购物车
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart cart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,cart);
        List<ShoppingCart> list = shoppingCartMapper.list(cart);
        ShoppingCart shoppingCart = list.get(0);
        shoppingCart.setNumber(shoppingCart.getNumber()-1);
        shoppingCartMapper.updateNumber(shoppingCart);
    }
}
