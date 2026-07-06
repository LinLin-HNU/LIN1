package com.sky.mapper;

import com.sky.entity.DishSimilarity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜品相似度Mapper接口
 */
@Mapper
public interface DishSimilarityMapper {

    /**
     * 批量插入菜品相似度数据
     * @param similarityList 相似度列表
     */
    void batchInsert(List<DishSimilarity> similarityList);

    /**
     * 删除所有相似度数据（用于定时任务重新计算前清空旧数据）
     */
    @Delete("DELETE FROM dish_similarity")
    void deleteAll();

    /**
     * 根据菜品ID查询最相似的Top N个菜品
     * @param dishId 菜品ID
     * @param limit 返回数量限制
     * @return 相似度列表，按相似度降序排列
     */
    @Select("SELECT ds.*, d.name as dishName, d.image as dishImage " +
            "FROM dish_similarity ds " +
            "JOIN dish d ON ds.dish_id_2 = d.id " +
            "WHERE ds.dish_id_1 = #{dishId} AND d.status = 1 " +
            "ORDER BY ds.similarity_score DESC " +
            "LIMIT #{limit}")
    List<DishSimilarity> getSimilarDishes(Long dishId, Integer limit);

    /**
     * 查询指定菜品对之间的相似度
     * @param dishId1 菜品1的ID
     * @param dishId2 菜品2的ID
     * @return 相似度对象
     */
    @Select("SELECT * FROM dish_similarity WHERE dish_id_1 = #{dishId1} AND dish_id_2 = #{dishId2}")
    DishSimilarity getSimilarity(Long dishId1, Long dishId2);
}
