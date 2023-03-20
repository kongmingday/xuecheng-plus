package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.UserMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/18 22:54
 */
@Service
public class UserMessageServiceImpl implements UserMessageService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private XcUserMapper xcUserMapper;

    @Override
    public void resetPassword(String user, String password) {
        LambdaQueryWrapper<XcUser> lqw = new LambdaQueryWrapper<>();
        if (user.contains("@")){
            lqw.eq(XcUser::getEmail, user);
        }else {
            lqw.eq(XcUser::getCellphone, user);
        }
        XcUser xcUser = xcUserMapper.selectOne(lqw);
        if (xcUser == null){
            throw new RuntimeException("账号不存在");
        }
        String encode = passwordEncoder.encode(password);
        xcUser.setPassword(encode);
        xcUserMapper.updateById(xcUser);
    }
}
