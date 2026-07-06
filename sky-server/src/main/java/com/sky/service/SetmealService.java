package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    List<Setmeal> list(Long categoryId);

    List<DishItemVO> dishList(Long id);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void save(SetmealDTO setmealDTO);

    void setStatus(Integer status, Long id);

    void delete(List<Long> ids);

    SetmealVO selectById(Long id);

    void update(SetmealDTO setmealDTO);
}
