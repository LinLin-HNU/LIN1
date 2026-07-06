package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户接口")
@Slf4j
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */



    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> Login(@RequestBody UserLoginDTO userLoginDTO)//向前端请求时，Dto是放在请求体中的
    {
        log.info("微信登录：{}", userLoginDTO.getCode());

        //1.通过前端微信小程序传来的登陆码code，在service层实现与微信服务器进行交互，获取openid并封装User对象
        User user = userService.wxLogin(userLoginDTO);

        //2.获取到User对象，但没有jwt：创建jwt
        /**
         注意：为什么要写UserId：键名 "id" 可以改为其他名称（如 "userId"），但必须确保：
         生成 JWT 和解析 JWT 时使用相同的键名。
         后续业务逻辑中能正确读取该键对应的值
         */
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put(JwtClaimsConstant.USER_ID, user.getId());
        String jwt = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), userMap);

        //3.返回UserLoginVO对象
        UserLoginVO loginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(jwt)
                .build();

        return Result.success(loginVO);

        /**
         * 在这里，在yml中配置的jwt的配置，会被封装在JwtProperties类中，因为这个类加了@ConfigurationProperties(prefix="sky.jwt")注解
         */

    }

}
