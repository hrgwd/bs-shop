package com.example.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.common.Result;
import com.example.entity.Goods;
import com.example.entity.Orders;
import com.example.mapper.GoodsMapper;
import com.example.mapper.OrdersMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.api-url}")
    private String apiUrl;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 获取用户全部订单（用于进入客服页时展示）
     */
    @GetMapping("/recent")
    public Result recent(@RequestParam(required = false) Integer userId) {
        if (userId == null || userId <= 0) {
            return Result.success(new ArrayList<>());
        }
        Orders query = new Orders();
        query.setUserId(userId);
        List<Orders> orders = ordersMapper.selectAll(query);
        return Result.success(orders);
    }

    /**
     * 发送消息给 AI 客服
     */
    @PostMapping("/send")
    public Result send(@RequestBody JSONObject params) {
        String message = params.getStr("message");
        Integer userId = params.getInt("userId");
        if (message == null || message.trim().isEmpty()) {
            return Result.error("400", "消息不能为空");
        }

        try {
            StringBuilder context = new StringBuilder();
            context.append("你是一个专业的电商平台客服助手，负责解答用户关于商品、订单、配送、退换货等问题，回答简洁友好。\n\n");

            if (userId != null && userId > 0) {
                // 全量订单（供 AI 回答所有订单相关问题）
                Orders query = new Orders();
                query.setUserId(userId);
                List<Orders> allOrders = ordersMapper.selectAll(query);
                if (allOrders != null && !allOrders.isEmpty()) {
                    context.append(String.format("【用户全部订单，共 %d 条】\n", allOrders.size()));
                    for (Orders o : allOrders) {
                        context.append(String.format(
                                "- 商品：%s，数量：%d，金额：%.2f元，状态：%s，订单号：%s\n",
                                o.getGoodsName() != null ? o.getGoodsName() : "未知商品",
                                o.getNum() != null ? o.getNum() : 0,
                                o.getPrice() != null ? o.getPrice() : 0.0,
                                o.getStatus() != null ? o.getStatus() : "未知",
                                o.getOrderId() != null ? o.getOrderId() : "-"
                        ));
                    }
                    context.append("\n");
                }
            }

            // 热门商品（供推荐）
            List<Goods> hotGoods = goodsMapper.selectTop15();
            if (hotGoods != null && !hotGoods.isEmpty()) {
                context.append("【平台热门商品（供推荐参考）】\n");
                int limit = Math.min(hotGoods.size(), 10);
                for (int i = 0; i < limit; i++) {
                    Goods g = hotGoods.get(i);
                    context.append(String.format(
                            "- %s，价格：%.2f元/%s，销量：%d\n",
                            g.getName(),
                            g.getPrice() != null ? g.getPrice() : 0.0,
                            g.getUnit() != null ? g.getUnit() : "件",
                            g.getCount() != null ? g.getCount() : 0
                    ));
                }
                context.append("\n");
            }

            context.append("请根据以上真实数据回答用户问题，如果数据中没有相关信息则给出通用建议。");

            JSONObject body = JSONUtil.createObj()
                    .set("model", "deepseek-chat")
                    .set("messages", JSONUtil.createArray()
                            .set(JSONUtil.createObj()
                                    .set("role", "system")
                                    .set("content", context.toString()))
                            .set(JSONUtil.createObj()
                                    .set("role", "user")
                                    .set("content", message)))
                    .set("stream", false);

            String response = HttpRequest.post(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(body.toString())
                    .timeout(30000)
                    .execute()
                    .body();

            JSONObject result = JSONUtil.parseObj(response);
            JSONArray choices = result.getJSONArray("choices");
            if (choices != null && !choices.isEmpty()) {
                String reply = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getStr("content");
                return Result.success(reply);
            }
            return Result.error("500", "AI 无响应，请稍后重试");

        } catch (Exception e) {
            return Result.error("500", "请求失败：" + e.getMessage());
        }
    }
}
