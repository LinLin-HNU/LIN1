package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userCategory")
@RequestMapping("/user/category")
@Api(tags="C类用户端分类接口")
@Slf4j
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类
     */

    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(value = "type",required = false) Integer type){
        log.info("查询分类");
        //若type=null，查询全部分类
        //若type==1，查询菜品分类
        //若type==2，查询套餐分类
        List<Category> categoryList = categoryService.list(type);
        return Result.success(categoryList);
    }
}
