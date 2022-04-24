package com.fwd.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fwd.seckill.exception.GlobalException;
import com.fwd.seckill.mapper.UserMapper;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.service.IUserService;
import com.fwd.seckill.utils.CookieUtil;
import com.fwd.seckill.utils.MD5Util;
import com.fwd.seckill.utils.UUIDUtil;
import com.fwd.seckill.utils.ValidatorUtil;
import com.fwd.seckill.vo.LoginVO;
import com.fwd.seckill.vo.RespBean;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.fwd.seckill.utils.CookieUtil.setCookie;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author fwd
 * @since 2022-03-28
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public RespBean login(HttpServletRequest request, HttpServletResponse response, LoginVO loginVO) {
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password))
            // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        if (!ValidatorUtil.isMobile(mobile))
            // return RespBean.error(RespBeanEnum.MOBILE_ERROR);
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR);
        // 根据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (user == null)
            // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        // 校验密码
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword()))
            // return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        // 生成cookie
        String ticket = UUIDUtil.uuid();
        // request.getSession().setAttribute(ticket, user);
        redisTemplate.opsForValue().set("user:" + ticket, user);
        setCookie(request, response, "userTicket", ticket);
        System.out.println("userTicket: " + ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response) {

        if (StringUtils.isEmpty(userTicket))
            return null;
        User user = ((User) redisTemplate.opsForValue().get("user:" + userTicket));
        if (null != user)
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, Long userId, String password) {
        User user = userMapper.selectById(userId);
        if (user == null)
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        user.setPassword(MD5Util.formPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (result == 1) {
            // 删除redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
