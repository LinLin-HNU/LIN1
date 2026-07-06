package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
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

    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> list(DishPageQueryDTO dishPageQueryDTO);

    /**
     *删除菜品时，看当前菜品是否状态为起售，是则不删
     * */
    @Select("select * from dish where id = #{id}")
    Dish selectByStatus(Long id);

    /**
    删除菜品时，看当前菜品是否和套餐有关联，有则不删
     */
    List<Long> selectByCatagory(List<Long> dishIds);

//    @Delete("delete from dish where id = #{id}")
    void delete(List<Long> ids);

    @Select("select d.*, c.name as categoryName from dish d left join category c on d.category_id=c.id where d.id=#{id};")
    DishVO selectById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(DishDTO dishDTO);

    @AutoFill(value = OperationType.UPDATE)
    void updateStatus(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<DishVO> selectByCategoryId(Long categoryId);

    /**
     * 查询菜品起售停售数量
     * @param i
     */
    @Select("select count(id) from dish where status=#{i}")
    Integer countByStatus(int i);
}
