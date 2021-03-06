package com.fwd.seckill.vo;

import com.fwd.seckill.pojo.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVO extends Goods {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    void test11(){}
    void test21(){}
    void test31(){}
    void test41(){}
    void test51(){}

    void test1(){}
    void test2(){}
    void test3(){}
    void test4(){}
    void test5(){}

    // 修bug
    void bug01(){}
    void bug02(){}

}
