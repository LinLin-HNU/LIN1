package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {


    List<ShoppingCart> list(ShoppingCart cart);

    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumber(ShoppingCart shoppingCart);


    void insert(ShoppingCart cart);

    @Select("select * from shopping_cart where user_id=#{currentId}")
    List<ShoppingCart> selectByUserId(Long currentId);

    @Delete("delete from shopping_cart where user_id=#{currentId}")
    void clean(Long currentId);
}
