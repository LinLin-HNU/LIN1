package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    @Select("select * from setmeal where category_id = #{categoryId}")
    List<Setmeal> list(Long categoryId);

    /**
     * 根据套餐id查询包含的菜品列表
     * @param setmealId
     * @return
     */
    @Select("select sd.name, sd.copies, d.image, d.description " +
            "from setmeal_dish sd left join dish d on sd.dish_id = d.id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> dishList(Long setmealId);

    /**
     * 分页查询
     * @param setmealPageQueryDTO
     * @return
     */

    Page<Setmeal> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);


//    void insertBatch(SetmealVO setmealVO);
// SetmealMapper.java
    void insertBatch(List<SetmealDish> setmealDishes);

/**
 * 更新套餐的起售停售状态
 * @param status
 * @param id
 */
    @Update("update setmeal set status=#{status} where id=#{id}")
    void setStatus(Integer status, Long id);

    /**
     *管理端套餐批量删除
     * @param id
     */
    @Delete("delete from setmeal where id=#{id}")
    void delete(Long id);


    @Select("select * from setmeal where id=#{id}")
    Setmeal selectById(Long id);

    @Select("select * from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> selectSDById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);


    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 查询套餐起售停售数量
     * @param i
     * @return
     */
    @Select("select count(id) from setmeal where status=#{i}")
    Integer selectByStatus(int i);
}
