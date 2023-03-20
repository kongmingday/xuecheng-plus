package com.xuecheng.auth.controller;

import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.FindPasswordParamsDto;
import com.xuecheng.ucenter.service.UserMessageService;
import com.xuecheng.ucenter.service.impl.RedisCheckCodeStore;
import com.xuecheng.ucenter.service.impl.UserMessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/18 22:00
 */
@RestController
@Slf4j
public class FindController {

    @Autowired
    private CheckCodeClient checkCodeClient;

    @Autowired
    private UserMessageService userMessageService;

    private final String findKey = "findKey";

    @PostMapping("/findpassword")
    public String resetPassword(@RequestBody FindPasswordParamsDto param){
        String phone = param.getCellphone();
        String email = param.getEmail();
        if (StringUtils.isEmpty(phone) && StringUtils.isEmpty(email)){
            throw new RuntimeException("手机和邮箱皆不能为空");
        }
        String key = StringUtils.isEmpty(phone) ? email : phone;
        Boolean verify = checkCodeClient.verify(findKey + ":" + key, param.getCheckcode());
        if (!verify){
            throw new RuntimeException("验证码有误");
        }
        String password = param.getPassword();
        String confirmpwd = param.getConfirmpwd();
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(confirmpwd)){
            throw new RuntimeException("密码或确认密码不能为空");
        }
        if (!password.equals(confirmpwd)){
            throw new RuntimeException("密码和确认密码不一致");
        }
        userMessageService.resetPassword(key, password);

        return "寻回成功";
    }
}
