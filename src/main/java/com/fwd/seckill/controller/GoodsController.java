package com.fwd.seckill.controller;

import com.fwd.seckill.pojo.User;
import com.fwd.seckill.service.IGoodsService;
import com.fwd.seckill.service.IUserService;
import com.fwd.seckill.utils.JsonUtil;
import com.fwd.seckill.vo.DetailVO;
import com.fwd.seckill.vo.GoodsVO;
import com.fwd.seckill.vo.RespBean;
import com.fwd.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;  // 用于手动渲染Thymeleaf

    /**
     * 跳转到商品列表页，QPS: 589.6/sec
     * @param model
     * @return
     */
//    @RequestMapping("/toList")
    public String toList01(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
//        if (StringUtils.isEmpty(ticket))
//            return "login";
        // User user = (User) session.getAttribute(ticket);
//        User user = ((User) userService.getByUserTicket(ticket, request, response));
        if (user == null)
            return "login";

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVO());
        return "goodsList";
    }

    /**
     * 跳转到商品列表页，页面缓存，QPS：2599.4/sec
     * @param request
     * @param response
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList02(HttpServletRequest request, HttpServletResponse response, Model model, User user) {

        if (user == null)
            return "login";

        //Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html))
            return html;

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVO());
        //return "goodsList";

        //如果为空，手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html))
            valueOperations.set("goodsList", html, 30, TimeUnit.SECONDS);
        return html;
    }

    /**
     * 跳转到商品详情页，无优化，QPS：346.5/sec
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
//    @GetMapping("/toDetail/{goodsId}")
    public String toDetail01(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId) {

        if (user == null)
            return "login";

        model.addAttribute("user", user);
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goods);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态，0表示倒计时中，1表示进行中，2表示已结束
        int secKillStatus = 0;
        // 剩余开始时间
        int remainSeconds = 0;
        // 剩余结束时间
        int endSeconds = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
        // 秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
        // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("endSeconds", endSeconds);
        return "goodsDetail";
    }

    /**
     *
     * 跳转到商品详情页，页面缓存，QPS：1226.1/sec
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/toDetail02/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail02(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable Long goodsId) {

        if (user == null)
            return "login";

        // Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetailHTML:" + goodsId);
        if (!StringUtils.isEmpty(html))
            return html;

        model.addAttribute("user", user);
        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goods);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        // 秒杀状态，0表示倒计时中，1表示进行中，2表示已结束
        int secKillStatus = 0;
        // 剩余开始时间
        int remainSeconds = 0;
        // 剩余结束时间
        int endSeconds = (int) ((endDate.getTime() - nowDate.getTime()) / 1000);
        // 秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        model.addAttribute("endSeconds", endSeconds);

        // 如果为空，手动渲染，存入Redis
        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if (!StringUtils.isEmpty(html))
            valueOperations.set("goodsDetailHTML:" + goodsId, html, 30, TimeUnit.SECONDS);

        return html;
    }

    /**
     * 跳转到商品详情页，页面静态化，QPS：1922.3/sec
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail03(User user, @PathVariable Long goodsId) {

        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        // Redis中获取DetailVO对象，如果不为空，直接返回DetailVO
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String detailVOJson = (String) valueOperations.get("goodsDetailVO:" + goodsId);
        if (!StringUtils.isEmpty(detailVOJson)) {
            DetailVO detailVO = JsonUtil.jsonStr2Object(detailVOJson, DetailVO.class);
            return RespBean.success(detailVO);
        }

        GoodsVO goods = goodsService.findGoodsVOByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds = 0;
        // 秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVO detailVo = new DetailVO();
        detailVo.setGoodsVO(goods);
        detailVo.setUser(user);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSecKillStatus(secKillStatus);
        detailVOJson = JsonUtil.object2JsonStr(detailVo);

        // 如果为空，存入Redis
        valueOperations.set("goodsDetailVO:" + goodsId, detailVOJson, 30, TimeUnit.SECONDS);

        return RespBean.success(detailVo);
    }
}
