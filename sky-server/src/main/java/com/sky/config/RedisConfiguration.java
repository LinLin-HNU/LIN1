package com.sky.config;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Slf4j
@Configuration
public class RedisConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("开始创建redis模板对象。。。");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis连接工厂
        redisTemplate.setConnectionFactory(connectionFactory);
        //设置redis key的序列化器
        //作用：Redis的key会以字符串形式存储，便于阅读和管理
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //AI推荐增设value的序列化器
        //-----------------------------------------------------------------------------------
        // 设置Value的序列化器 - JSON格式（推荐）
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        // 配置ObjectMapper，支持日期格式化等
         JacksonObjectMapper objectMapper = new JacksonObjectMapper();
         objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
         objectMapper.activateDefaultTyping(
         LaissezFaireSubTypeValidator.instance,
         ObjectMapper.DefaultTyping.NON_FINAL
         );
         jsonSerializer.setObjectMapper(objectMapper);

         redisTemplate.setValueSerializer(jsonSerializer);

        // 设置Hash类型的Key和Value序列化器
         redisTemplate.setHashKeySerializer(new StringRedisSerializer());
         redisTemplate.setHashValueSerializer(jsonSerializer);

         redisTemplate.afterPropertiesSet(); // 初始化配置

        //-----------------------------------------------------------------------------------

        return redisTemplate;
    }
}


