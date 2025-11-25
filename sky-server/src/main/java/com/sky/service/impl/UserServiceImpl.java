package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    // 微信登录URL
    public static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信登录
     *
     * @param userLoginDTO 用户登录信息
     * @return 登录成功的用户信息
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1. 调用微信接口，完成登录，获取openid
        String openid = getOpenIdByCode(userLoginDTO.getCode());

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        // 判断当前用户是否是新用户，则自动完成注册，否则直接登录
        User user = userMapper.getUserByOpenid(openid);
        if (user == null) {
            // 新用户，自动注册
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            log.info("微信新用户注册成功，用户信息：{}", user);
            return user;
        }else {
            // 老用户，直接登录
            log.info("微信老用户登录成功，用户信息：{}", user);
            return user;
        }
    }

    private String getOpenIdByCode(String code) {
        // 调用微信接口，完成登录
        Map<String,String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN_URL, map);
        // 判断openid是否存在，不存在则注册新用户
        JSONObject jsonObject = JSONObject.parseObject(json); // 将返回的json字符串转换为json对象
        return jsonObject.getString("openid");
    }
}
