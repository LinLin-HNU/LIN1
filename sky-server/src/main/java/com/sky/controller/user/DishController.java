package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "C类用户端菜品接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /** 缓存过期时间（分钟），redis中的缓存只能存货30min */
    private static final long CACHE_TTL_MINUTES = 30;
    /** 互斥锁超时时间（秒），防止死锁 ，如果持有锁的线程挂了，30s后锁自动释放，它是为了防止死锁，否则可能持锁线程永不释放锁*/
    private static final long LOCK_TIMEOUT_SECONDS = 30;
    /** 等待线程重试间隔（毫秒），非持锁线程最多50ms重试看redis缓存是否写入数据 */
    private static final long RETRY_INTERVAL_MS = 50;
    /** 最大重试次数 ，非持锁线程最多重试3次，超过就直接查数据库（兜底保护）*/
    private static final int MAX_RETRIES = 3;

    /**
     * 根据分类id查询菜品
     *
     * 使用 Redis 缓存 + 互斥锁方案防止缓存击穿：
     * 1. 查缓存命中 → 直接返回
     * 2. 缓存未命中 → 尝试获取互斥锁（SETNX）
     * 3. 获取锁成功 → 查数据库，重建缓存，释放锁
     * 4. 获取锁失败 → 休眠重试，等待其他线程重建缓存
     * 5. 重试超时 → 兜底查数据库（极低概率）
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品（Redis缓存+互斥锁防击穿）")
    public Result<List<DishVO>> list(Long categoryId) {
        String cacheKey = "dish_" + categoryId;

        // 1. 查缓存
        List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(cacheKey);
        if (list != null && !list.isEmpty()) {
            return Result.success(list);
        }

        // 2. 缓存未命中，尝试获取互斥锁（SETNX）防止缓存击穿
        String lockKey = "lock:" + cacheKey;
        String lockValue = UUID.randomUUID().toString();

        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (Boolean.TRUE.equals(locked)) {
            // 3. 获取锁成功 — 查数据库并重建缓存
            try {
                log.info("获取互斥锁成功，查询数据库：categoryId={}", categoryId);
                List<DishVO> dishVOList = dishService.list(categoryId);

                // 重建缓存，设置合理 TTL
                redisTemplate.opsForValue().set(cacheKey, dishVOList, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
                return Result.success(dishVOList);
            } finally {
                // 4. 释放锁（Lua 风格：只释放自己持有的锁，防止误删其他线程的锁）
                String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
                if (lockValue.equals(currentValue)) {
                    redisTemplate.delete(lockKey);
                }
                log.debug("互斥锁已释放：{}", lockKey);
            }
        } else {
            // 5. 未获取到锁 — 说明其他线程正在重建缓存，休眠重试等待
            log.info("未获取到互斥锁，休眠重试等待缓存重建：categoryId={}", categoryId);
            for (int i = 0; i < MAX_RETRIES; i++) {
                try {
                    Thread.sleep(RETRY_INTERVAL_MS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                // 重新检查缓存是否已被其他线程重建
                List<DishVO> cacheList = (List<DishVO>) redisTemplate.opsForValue().get(cacheKey);
                if (cacheList != null && !cacheList.isEmpty()) {
                    log.info("等待后缓存已重建，返回缓存数据：categoryId={}", categoryId);
                    return Result.success(cacheList);
                }
            }
            // 6. 兜底：多次重试后仍未拿到缓存，直接查数据库（概率极低）
            log.warn("重试{}次后缓存仍未重建，兜底查询数据库：categoryId={}", MAX_RETRIES, categoryId);
            List<DishVO> dishVOList = dishService.list(categoryId);
            return Result.success(dishVOList);
        }
    }

}
