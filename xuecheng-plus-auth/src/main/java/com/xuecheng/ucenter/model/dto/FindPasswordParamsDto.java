package com.xuecheng.ucenter.model.dto;

import lombok.Data;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/18 21:56
 */
@Data
public class FindPasswordParamsDto {

    private String cellphone;
    private String email;
    private String checkcodekey;
    private String checkcode;
    private String confirmpwd;
    private String password;

}
