package com.sky.service.impl;

import com.github.pagehelper.ISelect;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishFlavorDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Transactional
    @Override
    public void save(DishDTO dishDTO) {
        // 1. 保存 dish 主表 —— 关键：显式设置 categoryId
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setCategoryId(dishDTO.getCategoryId()); // ✅ 关联分类
        dishMapper.insert(dish);

        // 2. 保存口味（若存在）
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavorDTO : flavors) {
                DishFlavor dishFlavor = new DishFlavor();
                dishFlavor.setDishId(dish.getId());      // 关联刚插入的 dish.id
                dishFlavor.setName(flavorDTO.getName());
                dishFlavor.setValue(flavorDTO.getValue());
                dishFlavorMapper.insert(dishFlavor);
            }
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
//        // 构建查询条件
//        Dish dish = new Dish();
//
//        // 安全赋值：仅当非 null 时才设置
//        if (dishPageQueryDTO.getCategoryId() != null) {
//            dish.setCategoryId(Long.valueOf(dishPageQueryDTO.getCategoryId()));
//        }
//        dish.setName(dishPageQueryDTO.getName());
//        dish.setStatus(dishPageQueryDTO.getStatus());

        // 分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishList = dishMapper.list(dishPageQueryDTO);

//        long total = PageHelper.count((ISelect) dishList);
        return new PageResult(dishList.getTotal(), dishList);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        //1.判断当前菜品是否能删除 -- 是否存在起售中的菜品
        ids.forEach(id -> {
            Dish dish=dishMapper.selectByStatus(id);
            if(Objects.equals(dish.getStatus(), StatusConstant.ENABLE)){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        });
        //2.判断当前菜品是否能删除 -- 是否存在与套餐关联的菜品
        List<Long> dishIds=dishMapper.selectByCatagory(ids);
        if(dishIds.size()>0 && dishIds != null){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //3.批量删除菜品
//        for (Long id : ids) {
//            dishMapper.delete(id);
//            //4.删除菜品关联的口味
//            dishFlavorMapper.deleteByDishId(id);
//        }
        dishMapper.delete(ids);
        dishFlavorMapper.deleteByDishId(ids);   //dish表的id和dish_flavor表的dish_id是相关联的
    }

    @Override
    public DishVO selectById(Long id) {
        //1.查询基本的菜品信息
        DishVO dishVO = dishMapper.selectById(id);

        //2.查询菜品的口味信息，一个菜品可能不止一个口味，所以是口味数组
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(id);

        //3.返回数据
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        // 1. 修改 dish 主表
        dishMapper.update(dishDTO);

        // 2.删除原有的口味数据
        dishFlavorMapper.deleteByDishId(Collections.singletonList(dishDTO.getId()));

        //3.重新插入新的口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
            }
        }
        //批量插入
        dishFlavorMapper.insertBatch(flavors);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        //修改菜品状态
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateStatus(dish);
        //注意：如果想在mapper中使用@AutoFill注解，就必须传递一个dish对象，
        //@AutoFill是AOP切面注解，他会在方法执行前调用目标对象的 setUpdateTime(LocalDateTime) 和 setUpdateUser(Long) 方法
        //所以@AutiFill要求目标方法参数必须是实体对象，否则无法反射赋值
    }

    /**
     * 根据分类id查询菜品，返回值是菜品的集合，每个菜品对象中包含菜品的详细信息和菜品的口味信息
     * @param categoryId
     * @return
     */
    @Override
    public List<DishVO> list(Long categoryId) {
        //查询菜品的信息
        List<DishVO> dishVOList = dishMapper.selectByCategoryId(categoryId);


        //查询菜品的口味信息
        for (DishVO dishVO : dishVOList) {
            List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishId(dishVO.getId());
            dishVO.setFlavors(dishFlavors);
        }

        //返回结果
        return dishVOList;
    }


}
