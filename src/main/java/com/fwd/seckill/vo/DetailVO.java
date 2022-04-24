package com.fwd.seckill.vo;

import com.fwd.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVO {
    private User user;
    private GoodsVO goodsVO;
    private int secKillStatus;
    private int remainSeconds;
}