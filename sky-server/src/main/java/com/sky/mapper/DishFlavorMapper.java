package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    @AutoFill(value = OperationType.INSERT)
    void insert(DishFlavor dishFlavor);

//    @Delete("delete from dish_flavor where dish_id = #{id}")
    void deleteByDishId(List<Long> ids);

    List<DishFlavor> selectByDishId(Long id);

//    @AutoFill(value = OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);


//    void insert(DishFlavor dishFlavor);
}
