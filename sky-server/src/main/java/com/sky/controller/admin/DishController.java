package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * 菜品管理
 */
@RestController("adminDish")
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品管理接口")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result<String> save(@RequestBody DishDTO dishDTO) {
        //根据分类id清理缓存
        redisTemplate.delete("dish_" + dishDTO.getCategoryId());

        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }


    /*
    * 菜品分页查询
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    /*
    * 批量删除
     */
    @DeleteMapping
    @ApiOperation("批量删除")
    public Result deleteBatch(@RequestParam List<Long> ids){

        //清理所有缓存
//        Set keys = redisTemplate.keys("dish_*");
        // ✅ 推荐：使用scan命令，分批扫描
        Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions()
                        .match("dish_*")
                        .count(100)  // 每次扫描100个
                        .build()
        );
        List<String> keys = new ArrayList<>();
        while (cursor.hasNext()) {
            keys.add(cursor.next());
        }
        redisTemplate.delete(keys);

        log.info("批量删除：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    /*
    * 根据id查询菜品
     */

    @GetMapping("/{id}")
    public Result<DishVO> SelectById(@PathVariable Long id){
        log.info("查询菜品：{}", id);
        return Result.success(dishService.selectById(id));
    }

    /*
    * 修改菜品
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品：{}", dishDTO);

        //清理所有缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        dishService.update(dishDTO);
        return Result.success();
    }

    /*
    * 菜品起售停售
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售停售")
    public Result startOrStop(@PathVariable Integer status,
                              @RequestParam Long id){
        log.info("菜品起售停售：status={},id={}", status,id);

        //清理所有缓存
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        dishService.updateStatus(status, id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     */
    @ApiOperation("根据分类查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类查询菜品：{}", categoryId);
        return Result.success(dishService.list(categoryId));
    }
}
