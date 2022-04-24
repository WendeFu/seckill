package com.fwd.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 返回状态枚举
 */
@ToString
@Getter
@AllArgsConstructor
public enum RespBeanEnum {

    // 通用状态码
    SUCCESS(200, "success"),
    ERROR(500, "服务端异常"),
    // 登录模块
    SESSION_ERROR(500210, "用户不存在"),
    LOGIN_ERROR(500211, "用户名或密码错误"),
    MOBILE_ERROR(500212, "手机号码格式错误"),
    BIND_ERROR(500213, "参数校验异常"),
    MOBILE_NOT_EXIST(500214, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500215, "密码更新失败"),
    // 秒杀
    EMPTY_STOCK(500310, "库存不足"),
    REPEATE_ERROR(500311, "该商品每人限购1件"),
    // 订单
    ORDER_NOT_EXIST(500410, "订单不存在"),
    ;

    private final Integer code;
    private final String message;

}
