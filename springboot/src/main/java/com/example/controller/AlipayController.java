package com.example.controller;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.entity.Orders;
import com.example.service.OrdersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付宝沙箱支付接口
 */
@Controller
@RequestMapping("/alipay")
public class AlipayController {

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Value("${alipay.public-key}")
    private String alipayPublicKey;

    @Value("${alipay.gateway}")
    private String gateway;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    @Resource
    private OrdersService ordersService;

    /**
     * 发起支付：接收逗号拼接的 orderIds，汇总所有商品信息后跳转支付宝收银台
     */
    @GetMapping("/pay")
    public void pay(@RequestParam String orderIds, HttpServletResponse response) throws Exception {
        String[] idArray = orderIds.split(",");

        // 逐个查询，汇总总价和商品名
        double totalAmount = 0;
        StringBuilder subject = new StringBuilder();
        for (String orderId : idArray) {
            Orders query = new Orders();
            query.setOrderId(orderId.trim());
            List<Orders> list = ordersService.selectAll(query);
            if (list == null || list.isEmpty()) continue;
            Orders o = list.get(0);
            totalAmount += o.getPrice() == null ? 0 : o.getPrice();
            if (subject.length() > 0) subject.append("、");
            String name = o.getGoodsName() != null ? o.getGoodsName() : "商品";
            subject.append(name).append("×").append(o.getNum() != null ? o.getNum() : 1);
        }

        if (subject.length() == 0) {
            response.getWriter().write("订单不存在");
            return;
        }

        AlipayClient alipayClient = new DefaultAlipayClient(
                gateway, appId, privateKey, "json", "UTF-8", alipayPublicKey, "RSA2"
        );

        // out_trade_no 用第一个 orderId（支付宝要求唯一不重复）
        String outTradeNo = idArray[0].trim();

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        // returnUrl 动态追加 orderIds 参数，同步回跳时用于更新所有关联订单
        request.setReturnUrl(returnUrl + "?orderIds=" + URLEncoder.encode(orderIds, "UTF-8"));
        request.setBizContent("{"
                + "\"out_trade_no\":\"" + outTradeNo + "\","
                + "\"total_amount\":\"" + String.format("%.2f", totalAmount) + "\","
                + "\"subject\":\"" + subject + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\","
                + "\"passback_params\":\"" + URLEncoder.encode(orderIds, "UTF-8") + "\""
                + "}");

        String form = alipayClient.pageExecute(request).getBody();
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(form);
        response.getWriter().flush();
    }

    /**
     * 支付宝同步回跳中转接口（returnUrl）
     * 从支付宝参数中取 out_trade_no，更新对应订单状态后重定向到前端订单页
     */
    @GetMapping("/return")
    public void returnPage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 优先从自定义参数 orderIds 获取所有订单号（购物车批量下单场景）
        String orderIds = request.getParameter("orderIds");
        if (orderIds != null && !orderIds.isEmpty()) {
            ordersService.paySuccess(orderIds);
        } else {
            // 兜底：只有 out_trade_no（单条订单）
            String outTradeNo = request.getParameter("out_trade_no");
            if (outTradeNo != null && !outTradeNo.isEmpty()) {
                ordersService.paySuccess(outTradeNo);
            }
        }
        // 重定向到前端订单页
        response.sendRedirect("http://localhost:8080/front/orders");
    }

    /**
     * 支付宝异步回调（notifyUrl）
     * 支付宝服务器主动 POST 通知，验签后更新订单状态
     */
    @ResponseBody
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 获取支付宝POST过来的全部参数
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }

        try {
            // 验签
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params, alipayPublicKey, "UTF-8", "RSA2"
            );
            if (signVerified) {
                String tradeStatus = params.get("trade_status");
                // 支付成功：TRADE_SUCCESS 或 TRADE_FINISHED
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    // 优先从 passback_params 获取完整的 orderIds（支持批量下单场景）
                    String passbackParams = params.get("passback_params");
                    if (passbackParams != null && !passbackParams.isEmpty()) {
                        String allOrderIds = URLDecoder.decode(passbackParams, "UTF-8");
                        ordersService.paySuccess(allOrderIds);
                    } else {
                        
                        String outTradeNo = params.get("out_trade_no");
                        ordersService.paySuccess(outTradeNo);
                    }
                }
                return "success"; // 必须返回 success，否则支付宝会重复通知
            } else {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }
}
