package com.qinglan.sdk.server.presentation.basic;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.qinglan.sdk.server.presentation.basic.dto.*;
import com.qinglan.sdk.server.common.HeepayTradeConfig;
import com.qinglan.sdk.server.common.JsonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.qinglan.sdk.server.common.HttpUtils;
import com.qinglan.sdk.server.application.basic.AccountService;

@Controller
public class AccountController {
    private static Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Resource
    private AccountService accountService;

    @RequestMapping("/account/initial")
    @ResponseBody
    public Map<String, Object> initial(InitialPattern initial) {
        logger.debug("params: {}", initial);
        return accountService.initial(initial);
    }

    @RequestMapping("/account/login")
    @ResponseBody
    public Map<String, Object> login(LoginPattern login) {
        logger.debug("params: {}", login);
        return accountService.login(login);
    }

    @RequestMapping("/account/heartbeat")
    @ResponseBody
    public Map<String, Object> heartbeat(HeartbeatPattern heartbeat) {
        logger.debug("params: {}", heartbeat);
        return accountService.heartbeat(heartbeat);
    }

    @RequestMapping("/account/logout")
    @ResponseBody
    public Map<String, Object> logout(LogoutPattern logout) {
        logger.debug("params: {}", logout);
        return accountService.logout(logout);
    }

    @RequestMapping("/account/quit")
    @ResponseBody
    public Map<String, Object> quit(QuitPattern quit) {
        logger.debug("params: {}", quit);
        return accountService.quit(quit);
    }

    @RequestMapping("/account/role/establish")
    @ResponseBody
    public Map<String, Object> roleEstablish(RoleCreatePattern roleEstablish) {
        logger.debug("params: {}", roleEstablish);
        return accountService.roleCreate(roleEstablish);
    }

    @RequestMapping("/account/order/generate")
    @ResponseBody
    public Map<String, Object> orderGenerate(OrderGenerateRequest orderGenerate) {
        logger.debug("params: {}", orderGenerate);
        return accountService.orderGenerate(orderGenerate);
    }

    @RequestMapping("/account/test/notify")
    @ResponseBody
    public String testNotify(HttpServletRequest request) {
        logger.debug("params: {}", request);
        return "0";
    }

    //IOS订单支付成功通知
    @RequestMapping("/order/success/notify")
    @ResponseBody
    public String orderSuccessNotify(HttpServletRequest request) {
        logger.debug("params: {}", HttpUtils.getRequestParams(request).toString());
        return accountService.orderSuccessNotify(request);
    }

    @RequestMapping("/account/order/self")
    @ResponseBody
    public Map<String, Object> selfOrderGenerate(HttpServletRequest request, OrderGenerateRequest orderGenerate) {
        logger.debug("params: {}", orderGenerate);
        orderGenerate.setIp(request.getRemoteAddr());
        return accountService.selforderGenerate(orderGenerate);
    }

    @RequestMapping("/account/selfpay/notify")
    @ResponseBody
    public String selfpayNotify(HttpServletRequest request) {
        logger.debug("params: {}", request);
        return accountService.payNotify(request);
    }

    @RequestMapping("/account/initsdk")
    @ResponseBody
    public String sdkinit(HttpServletRequest request) {
        logger.debug("zhidianparams: {}", request.getParameter("channelId"));
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("code", HeepayTradeConfig.getInstance().getSelfPay());
        return JsonMapper.toJson(m);
    }

    @RequestMapping("/account/gettoken")
    @ResponseBody
    public Map<String, Object> loginSuccess(LoginSuccessPattern loginSuccessPattern) {
        logger.debug("zhidianparams: {}", loginSuccessPattern);
        return accountService.loginSuccess(loginSuccessPattern);
    }

    @RequestMapping("/cp/getuserid")
    @ResponseBody
    public Map<String, Object> getUserId(GetUserInfoPattern pattern) {
        logger.debug("zhidianparams: {}", pattern);
        return accountService.getUserIdByToken(pattern);
    }

    @RequestMapping("/account/order/query")
    @ResponseBody
    public Map<String, Object> queryOrderStatus(QueryOrderRequest request) {
        logger.debug("query order: {}", request);
        return accountService.queryOrder(request);
    }
}
