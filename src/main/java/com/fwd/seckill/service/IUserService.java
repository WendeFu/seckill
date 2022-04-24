package com.fwd.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fwd.seckill.pojo.User;
import com.fwd.seckill.vo.LoginVO;
import com.fwd.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author fwd
 * @since 2022-03-28
 */
public interface IUserService extends IService<User> {



    /**
     * 登录
     * @param loginVO
     * @return
     */
    RespBean login(HttpServletRequest request, HttpServletResponse response, LoginVO loginVO);

    User getByUserTicket(String userTicket, HttpServletRequest request, HttpServletResponse response);

    RespBean updatePassword(String userTicket, Long userId, String password);
}
