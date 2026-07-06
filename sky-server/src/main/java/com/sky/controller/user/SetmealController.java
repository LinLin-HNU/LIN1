package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmeal")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C类用户端套餐接口")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private RedisTemplate redisTemplate;



    /**
     * 根据分类id查询套餐
     */

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")//缓存套餐数据 key: setmealCache::1，value就是返回结果List<Setmeal>，正好一个分类一份缓存
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("查询套餐：{}", categoryId);
        //--------------------------------------------------------------------------------
        /**
        String key = "setmealCache"+categoryId;
        List<Setmeal> list = (List<Setmeal>)redisTemplate.opsForValue().get(key);
        if(list!=null && list.size()>0){
            log.info("从缓存中取出分类id为setmealCache"+categoryId+"的套餐数据");
            return Result.success(list);
        }
        log.info("从数据库中查询分类id为setmealCache"+categoryId+"的套餐数据");
        List<Setmeal> setmealList = setmealService.list(categoryId);
        redisTemplate.opsForValue().set(key, setmealList);
        return Result.success(setmealList);
        */
        //--------------------------------------------------------------------------------
        return Result.success(setmealService.list(categoryId));
    }


    /**
     * 根据套餐id查询包含的菜品
     */
    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> dishList(Long id) {
        log.info("查询套餐包含的菜品：{}", id);
        //--------------------------------------------------------------------------------
        /**
        String key = "dish_"+id;
        List<DishItemVO> list = (List<DishItemVO>) redisTemplate.opsForValue().get(key);
        if(list!=null && list.size()>0){
            log.info("从缓存中取出套餐id为dish"+id+"的菜品数据");
            return Result.success(list);
        }
        log.info("从数据库中查询套餐id为dish"+id+"的菜品数据");
        list = setmealService.dishList(id);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
        */
        //--------------------------------------------------------------------------------
        return Result.success(setmealService.dishList(id));
    }




}
