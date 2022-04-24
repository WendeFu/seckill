package com.fwd.seckill.vo;

import com.fwd.seckill.pojo.Order;
import lombok.AllArgsConstructor; import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailVO {
    private Order order;
    private GoodsVO goodsVO;
}
