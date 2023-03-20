package com.xuecheng.ucenter.service;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/16 0:34
 */
public interface AuthService {

    XcUserExt execute(AuthParamsDto authParamsDto);
}
