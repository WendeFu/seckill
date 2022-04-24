package com.fwd.seckill.exception;

import com.fwd.seckill.vo.RespBean;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception exception) {
        if (exception instanceof GlobalException) {
            GlobalException globalException = (GlobalException) exception;
            return RespBean.error(globalException.getRespBeanEnum());
        } else if (exception instanceof BindException) {
            BindException bindException = (BindException) exception;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            respBean.setMessage("参数校验异常：" + bindException.
                    getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        System.out.println(exception.toString());
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
