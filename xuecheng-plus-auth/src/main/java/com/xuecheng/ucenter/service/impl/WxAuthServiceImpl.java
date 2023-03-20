package com.xuecheng.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.mapper.XcUserRoleMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.model.po.XcUserRole;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * @author eotouch
 * @version 1.0
 * @description TODO
 * @date 2023/03/16 0:36
 */

@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {

    @Autowired
    private XcUserMapper xcUserMapper;
    @Value("${weixin.secret}")
    String secret;
    @Value("${weixin.appid}")
    String appid;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxAuthServiceImpl wxAuthService;

    @Autowired
    private XcUserRoleMapper xcUserRoleMapper;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        //账号
        String username = authParamsDto.getUsername();
        XcUser user = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (user == null) {
            //返回空表示用户不存在
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(user, xcUserExt);

        return xcUserExt;
    }


    public XcUser wxAuth(String code) {
        //获取access_token
        Map<String, String> access_token = getAccess_token(code);
        //获取用户信息
        String openid = access_token.get("openid");
        String accessToken = access_token.get("access_token");
        Map<String, String> userinfo = getUserinfo(accessToken, openid);
        //添加用户到数据库
        XcUser xcUser = wxAuthService.addWxUser(userinfo);
        return xcUser;
    }

    /**
     * @description 获取access_token
     * @param code 授权码
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author eotouch
     * @date 2023-03-17 20:52
     */
    private Map<String,String> getAccess_token(String code){
        // url填充
        String urlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(urlTemplate, appid, secret, code);
        // 远程调用
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String body = exchange.getBody();
        return JSON.parseObject(body, Map.class);
    }

    /**
     * @description 获取用户信息
     * @param access_token
     * @param openid
     * @return java.util.Map<java.lang.String,java.lang.String>
     * @author eotouch
     * @date 2023-03-17 20:53
     */
    private Map<String,String> getUserinfo(String access_token,String openid){
        String urlTemplate = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String url = String.format(urlTemplate, access_token, openid);

        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return JSON.parseObject(result, Map.class);
    }

    @Transactional
    public XcUser addWxUser(Map userInfo_map){
        String unionid = userInfo_map.get("unionid").toString();
        //根据unionid查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if(xcUser!=null){
            return xcUser;
        }
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo_map.get("nickname").toString());
        xcUser.setUserpic(userInfo_map.get("headimgurl").toString());
        xcUser.setName(userInfo_map.get("nickname").toString());
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }


}
