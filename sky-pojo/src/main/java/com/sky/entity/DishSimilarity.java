package com.sky.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品相似度实体类
 * 用于存储基于物品协同过滤算法计算的菜品相似度
 */
@Data
@Builder
/**
 * @Builder注解的作用是实现构造者模式
 * 传统：
 * xxx.serId();
 * xxx.setUser();
 * @Builder：
 * xxx.builder().serId().setUser().build();
 * 它会自动生成一些代码替代传统的代码
 */
@NoArgsConstructor
@AllArgsConstructor
public class DishSimilarity implements Serializable {

    /**
     * Serializable的作用是把该类实现的对象，序列化为二进制流，便于Spring MVC返回二进制->JSON字符串给前端
     * 便于Redis要序列化对象才能存入Redis，
     * 便于会话Session存储，也需要把对象序列化后再进行存储
     * 便于远程方法调用（如微服务）
     * 此外，如果项目将来要拆分为微服务，对象需要跨网络传出，就要序列化
     */

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 菜品1的ID
     */
    private Long dishId1;

    /**
     * 菜品2的ID
     */
    private Long dishId2;

    /**
     * 相似度分数（余弦相似度，范围0-1）
     * 值越接近1表示两个菜品越相似
     */
    private BigDecimal similarityScore;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
