package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/16 0:36
 */

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        String key = authParamsDto.getCheckcodekey();
        String value = authParamsDto.getCheckcode();
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)){
            throw new RuntimeException("验证码不能为空");
        }

        Boolean verify = checkCodeClient.verify(key, value);
        if (verify == null || !verify){
            throw new RuntimeException("验证码错误");
        }

        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, authParamsDto.getUsername()));
        if(user==null){
            throw new RuntimeException("账户不存在");
        }

        String passwordDb  =user.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches){
            throw new RuntimeException("账号或密码错误");
        }

        XcUserExt result = new XcUserExt();
        BeanUtils.copyProperties(user, result);
        return result;
    }
}
