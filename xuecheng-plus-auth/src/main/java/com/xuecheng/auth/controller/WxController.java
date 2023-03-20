package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import com.xuecheng.ucenter.service.impl.WxAuthServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * @author eotouch
 * @version 1.0
 * @description 微信认证接口
 * @date 2023/03/16 22:35
 */

@Slf4j
@Controller
public class WxController {

    @Autowired
    private WxAuthServiceImpl wxAuthService;

    @RequestMapping("/wxLogin")
    public String wxLogin(String code, String state) throws IOException {
        XcUser xcUser = wxAuthService.wxAuth(code);
        if (xcUser == null) {
            return "redirect:http://www.xuecheng-plus.com/error.html";
        }
        String username = xcUser.getUsername();
        return "redirect:http://www.xuecheng-plus.com/sign.html?username=" + username + "&authType=wx";
    }
}
