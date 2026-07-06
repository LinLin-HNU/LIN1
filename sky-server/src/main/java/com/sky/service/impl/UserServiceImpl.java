package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    public static final String URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     * 通过调用工具类中的doGet方法，与微信接口进行交互，获取openid
     * 并封装User对象返回：新用户就创建信息存库，老用户就查询信息
     * @param userLoginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {


        //1.调用微信接口，获取openid
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid",weChatProperties.getAppid());
        paramMap.put("secret", weChatProperties.getSecret());
        paramMap.put("js_code", userLoginDTO.getCode());
        paramMap.put("grant_type", "authorization_code");
        String get = HttpClientUtil.doGet(URL, paramMap);

        /**
         * 从微信官方接口返回的是JSON格式字符串，接下来要解析该字符串
         * 注意parseObject()方法，用的是fastjson包下的JSON类
         */
        JSONObject jsonObject = JSON.parseObject(get);//解析字符串
        String openid = jsonObject.getString("openid");//获取openid

        //2.判断openid是否为空，为空是和微信接口的交互有问题，但总之是登录失败了,要抛出异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }


        //3.判断当前用户是否为新用户,如果有就查询到了user对象
        User user = userMapper.select(openid);


        //4.如果是新用户，创建新用户
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            /**
             * 注意，插入完这个User对象后，要返回id值，因为在controller中要根据这个id来生成jwt令牌
             */
            userMapper.insert(user);
        }



        //5.返回用户对象
        return user;
    }
}
