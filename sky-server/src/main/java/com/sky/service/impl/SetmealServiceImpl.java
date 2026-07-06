package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public List<Setmeal> list(Long categoryId) {
        return setmealMapper.list(categoryId);
    }

    @Override
    public List<DishItemVO> dishList(Long id) {
        return setmealMapper.dishList(id);
    }

    /**
     * 管理端套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        //查询Setmeal
        Page<Setmeal> list = setmealMapper.pageQuery(setmealPageQueryDTO);

        //组装返回结果
        PageResult pageResult = new PageResult(list.getTotal(), list);
        return pageResult;
    }

    /**
     * 管理端新增套餐
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
//        setmealMapper.insert(setmealDTO);
////        setmealMapper.insertBatch(setmealVO);
//        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
//        if (dishes != null && !dishes.isEmpty()) {
//            setmealMapper.insertBatch(dishes); // 参数改为 List<SetmealDish>
//        }

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //向套餐表插入数据
        setmealMapper.insert(setmeal);
        /**
         * ！！！id是主键自增的，前端不会把setmeal表的id属性也传过来
         * 所以先insert后才能得到setmeal的id值，然后setmeal的id和setmeal_dish的sermeal_id相关联
         * 所以必须先存setmeal数据，得到id值，然后再把id值赋给setmealDish，然后再插入setmealDish剩下的数据
         */
        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setmealMapper.insertBatch(setmealDishes);

    }

    @Override
    public void setStatus(Integer status, Long id) {
        setmealMapper.setStatus(status,id);
    }

    @Override
    public void delete(List<Long> ids) {
        for (Long id : ids) {
            setmealMapper.delete(id);
        }
    }

    @Override
    public SetmealVO selectById(Long id) {
        //找到setmeal_dish集合的数据
        List<SetmealDish> list = setmealMapper.selectSDById(id);
        SetmealVO setmealVO = new SetmealVO();
        setmealVO.setSetmealDishes(list);
        //找到setmeal表的数据
        Setmeal setmeal = setmealMapper.selectById(id);
        BeanUtils.copyProperties(setmeal,setmealVO);
        /**
         * BeanUtil是Spring FrameWork官方提供的工具类
         * copyProperties是属性映射，相当于一串的set...赋值语句
         * 在这里copyProperties的作用是把Setmeal的所有属性值赋值给SetmealVo
         * copyProperties(A,B)，把前面的赋值给后面的
         */
        return setmealVO;
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
//        List<SetmealDish> list = setmealDTO.getSetmealDishes();
//        Setmeal setmeal = new Setmeal();
//        setmealMapper.updateSetmeal(setmeal);
//        setmealMapper.updateSetmealDish(list);

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        //1、修改套餐表，执行update
        setmealMapper.updateSetmeal(setmeal);

        //套餐id
        Long setmealId = setmealDTO.getId();

        //2、删除套餐和菜品的关联关系，操作setmeal_dish表，执行delete
        /**
         * ！！！setmeal表直接更新即可，setmeal_dish必须先删再插入
         */
        setmealMapper.deleteBySetmealId(setmealId);
        /**
         * 在前端选中关联的菜品后，dish_id,name,price,copies都是前端输入或按键后得到的数据
         * 但是id和setmeal_id并不是由前端按按输入能得到的
         * sermeal_id要从setmeal表中的id字段得到，所以接下来要先设置setmealId值
         */
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        //3、重新插入套餐和菜品的关联关系，操作setmeal_dish表，执行insert
        setmealMapper.insertBatch(setmealDishes);
    }
}
