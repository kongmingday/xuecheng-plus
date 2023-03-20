package com.xuecheng.ucenter.service;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/18 22:55
 */
public interface UserMessageService {

    /**
     * @description TODO
     * @param user 用户账号
     * @param password 密码
     * @return void
     * @author eotouch
     * @date 2023-03-18 22:56
     */
    void resetPassword(String user, String password);
}
