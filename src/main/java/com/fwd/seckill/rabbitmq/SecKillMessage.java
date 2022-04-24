package com.fwd.seckill.rabbitmq;

import com.fwd.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillMessage {
    private User user;
    private Long goodsId;
}
