package com.chippay.demo.controller;

import com.chippay.demo.common.constant.Consts;
import com.chippay.demo.domain.vo.GetOrderInfoVO;
import com.chippay.demo.domain.vo.GetUserOrderInfoVO;
import com.chippay.sdk.ApiV1;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.chippay.sdk.util.Utils;

/**
 * v1 版本使用的是对称签名，支持md5,sha256 通过参数signType 来区分，signType 0或者不传用的md5 签名，1为hmac_sha256 方式签名
 * v1 版本后续逐渐会废弃，有些接口已经不支持v1
 */
@RestController
@RequestMapping("/v1")
@Slf4j
public class DemoV1Controller {

    private  static Logger logger = LoggerFactory.getLogger(DemoV1Controller.class);

    @GetMapping("/getPrice")
    public String queryPrice(@RequestParam String coinType) throws Exception{
        return Utils.httpGet(Consts.AppConfig.URL+"/cola/quotePriceBusiness/priceConfig/getPrice?coinType="+coinType);
    }

    @PostMapping("/getUserOrderInfo")
    public String getUserOrderInfo(@RequestBody @Validated(value = GetUserOrderInfoVO.GetUserOrderInfo.class) GetUserOrderInfoVO getUserOrderInfoVO) throws Exception{

        Map<String, Object> params = new HashMap<>();
        params.put("companyId", Consts.AppConfig.APP_KEY);
        params.put("areaCode", getUserOrderInfoVO.getAreaCode());
        params.put("phone", getUserOrderInfoVO.getPhone());


        ApiV1 apiV1 = new ApiV1(Consts.AppConfig.APP_SECRET);

        return apiV1.call(Consts.AppConfig.URL+"/cola/order/common/getUserOrderInfo",params);

    }

    @PostMapping("/getOrderInfo")
    public String getOrderInfo(@RequestBody @Validated(value = GetOrderInfoVO.GetOrderInfo.class) GetOrderInfoVO getOrderInfoVO) throws Exception{

        Map<String, Object> params = new HashMap<>();
        params.put("companyId", Consts.AppConfig.APP_KEY);
        params.put("companyOrderNum", getOrderInfoVO.getCompanyOrderNum());
        ApiV1 apiV1 = new ApiV1(Consts.AppConfig.APP_SECRET);
        return apiV1.call(Consts.AppConfig.URL+"/cola/order/common/getOrderInfo",params);

    }

    @GetMapping("/buyOrder")
    public String buyOrder() throws Exception{

        Map<String,Object> params = new HashMap<>();

        params.put("username","haha");
        params.put("areaCode","86");
        params.put("phone","18900000008");
        params.put("idCardType","1");
        params.put("idCardNum","430524143201097878");
        params.put("kyc",2);
        params.put("companyOrderNum","NB"+System.currentTimeMillis());
        params.put("coinSign","USDT");
        params.put("coinAmount",20);
        params.put("total",200);
        params.put("orderPayChannel",3);
        params.put("payCoinSign","cny");
        params.put("companyId","12511234561");
        params.put("orderTime",System.currentTimeMillis());
        params.put("orderType",1);
        params.put("signType",1);
        params.put("syncUrl","http://127.0.0.1:8088");
        params.put("asyncUrl","http://127.0.0.1:8088/v1/callback");

        ApiV1 apiV1 = new ApiV1(Consts.AppConfig.APP_SECRET);

        return apiV1.call(Consts.AppConfig.URL+"/cola/order/addOrder",params);

    }


    @GetMapping("/sellOrder")
    public String sellOrder() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("areaCode", "86");
        params.put("asyncUrl", "http://127.0.0.1:8088/v1/callback");
        params.put("total", "200");
        params.put("coinSign", "USDT");
        params.put("coinAmount", "20");
        params.put("companyId", Consts.AppConfig.APP_KEY);
        params.put("companyOrderNum", UUID.randomUUID().toString());//商户自定义订单号
        params.put("idCardNum", "430524143201097878");
        params.put("idCardType", "1");
        params.put("kyc", "2");
        params.put("orderPayChannel", "3");
        params.put("orderTime",System.currentTimeMillis());
        params.put("orderType", "2");
        params.put("payCoinSign", "cny");
        params.put("phone", "18900000008");
        params.put("syncUrl", "http://127.0.0.1:8088");
        params.put("username", "范思哲");
        params.put("payCardNo", "99999999999999999999999");

        ApiV1 apiV1 = new ApiV1(Consts.AppConfig.APP_SECRET);
        return apiV1.call(Consts.AppConfig.URL+"/cola/order/addOrder",params);

    }

    @PostMapping("/getSign")
    public String getSign(@RequestBody HashMap<String,String> body,@RequestParam  String secretKey,@RequestParam Integer signType) throws Exception{

        body.remove("sign");
        body.put("secretKey",secretKey);
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        log.info("base={}",baseString);
        resp.put("baseString",baseString);

        if(signType == 1){
            String sign = Utils.HMACSHA256(baseString,secretKey);
            resp.put("sign",sign);
        }else{
            String sign = Utils.md5(baseString);
            resp.put("sign",sign);
        }
        return Utils.mapper.writeValueAsString(resp);
    }

    @PostMapping("/verifySign")
    public String verifySign(@RequestBody HashMap<String,String> body,@RequestParam  String secretKey,@RequestParam Integer signType) throws Exception{

        String sign = body.get("sign");
        body.remove("sign");
        body.put("secretKey",secretKey);
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        String sign1;
        if(signType == 0) { //md5
            sign1 = Utils.md5(baseString);
        }else{ //sha256
            sign1 = Utils.HMACSHA256(baseString, Consts.AppConfig.APP_SECRET);
        }
        log.info("base={},sign={},sign1={}",baseString,sign,sign1);

        if(!sign1.equals(sign) ){
            resp.put("code",500);
            resp.put("success",false);
        }
        return Utils.mapper.writeValueAsString(resp);
    }

    @PostMapping("/callback")
    public String callback(@RequestBody HashMap<String,String> body) throws Exception{

        //通过HashMap<String,String> 接受回调参数，不会造成浮点数据精度问题，从而导致签名计算不正确问题
        //或者通过Pojo 来接收回调参数，对应的浮点字段用 BigDecimal
        String sign = body.get("sign");
        body.remove("sign");
        body.put("secretKey",Consts.AppConfig.APP_SECRET);
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        String sign1;
        if(sign.length() == 32) //md5 {
        {
            sign1 = Utils.md5(baseString);
        }else{ //sha256
            sign1 = Utils.HMACSHA256(baseString, Consts.AppConfig.APP_SECRET);
        }
        log.info("base={},sign={},sign1={}",baseString,sign,sign1);

        if(!sign1.equals(sign) ){
            resp.put("code",500);
            resp.put("success",false);
        }
        return Utils.mapper.writeValueAsString(resp);
    }
}
