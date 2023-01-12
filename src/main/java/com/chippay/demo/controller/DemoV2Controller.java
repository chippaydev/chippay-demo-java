package com.chippay.demo.controller;

import com.chippay.demo.common.constant.Consts;
import com.chippay.demo.domain.vo.GetOrderInfoVO;
import com.chippay.demo.domain.vo.GetUserOrderInfoVO;
import com.chippay.sdk.ApiV1;
import com.chippay.sdk.ApiV2;
import com.chippay.sdk.util.Utils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * v2 版本使用的是非对称签名，商户端自己持有私钥，平台持有对应的公钥 签名方法为 SHA256withRSA
 * v2 版本的回调签名验证，需要用平台给的公钥验证签名
 */
@RestController
@RequestMapping("/v2")
@Slf4j
public class DemoV2Controller {

    private  static Logger logger = LoggerFactory.getLogger(DemoV2Controller.class);

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


        ApiV2 apiV2 = new ApiV2(Consts.AppConfig.APP_PRIVATE_KEY);

        return apiV2.call(Consts.AppConfig.URL+"/cola/apiOpen/common/getUserOrderInfo",params);

    }

    @PostMapping("/getOrderInfo")
    public String getOrderInfo(@RequestBody @Validated(value = GetOrderInfoVO.GetOrderInfo.class) GetOrderInfoVO getOrderInfoVO) throws Exception{

        Map<String, Object> params = new HashMap<>();
        params.put("companyId", Consts.AppConfig.APP_KEY);
        params.put("companyOrderNum", getOrderInfoVO.getCompanyOrderNum());
        ApiV2 apiV2 = new ApiV2(Consts.AppConfig.APP_PRIVATE_KEY);
        return apiV2.call(Consts.AppConfig.URL+"/cola/apiOpen/common/getOrderInfo",params);



    }

    @GetMapping("/addBuyOrder")
    public String addBuyOrder() throws Exception{

        Map<String,Object> params = new HashMap<>();

        params.put("username","haha");
        params.put("areaCode","86");
        params.put("phone","18900000007");
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
        params.put("orderType",1); //buy order
        params.put("syncUrl","http://127.0.0.1:8088");
        params.put("asyncUrl","http://127.0.0.1:8088/v2/callback");

        ApiV2 apiV2 = new ApiV2(Consts.AppConfig.APP_PRIVATE_KEY);

        return apiV2.call(Consts.AppConfig.URL+"/cola/apiOpen/addOrder",params);

    }

    @GetMapping("/addSellOrder")
    public String addSellOrder() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("areaCode", "86");
        params.put("asyncUrl", "http://127.0.0.1:8088/v2/callback");
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
        params.put("orderType", "2"); //sell order
        params.put("payCoinSign", "cny");
        params.put("phone", "18900000007");
        params.put("syncUrl", "http://127.0.0.1:8088");
        params.put("username", "范思哲");
        params.put("payCardNo", "99999999999999999999999");

        ApiV2 apiV2 = new ApiV2(Consts.AppConfig.APP_PRIVATE_KEY);

        return apiV2.call(Consts.AppConfig.URL+"/cola/apiOpen/addOrder",params);

    }

    @GetMapping("/addIntentOrder")
    public String addIntentOrder() throws Exception{
        Map<String, Object> params = new HashMap<>();
        params.put("areaCode", "86");
        params.put("asyncUrl", "http://127.0.0.1:8088/v2/callback");
        params.put("coinQuantity", "20");
        params.put("companyId", Consts.AppConfig.APP_KEY);
        params.put("companyOrderNum", UUID.randomUUID().toString());//商户自定义订单号
        params.put("phone", "18900000007");
        params.put("syncUrl", "http://127.0.0.1:8088");

        ApiV2 apiV2 = new ApiV2(Consts.AppConfig.APP_PRIVATE_KEY);

        return apiV2.call(Consts.AppConfig.URL+"/cola/apiOpen/addIntentOrder",params);

    }


    @PostMapping("/getSign")
    public String getSign(@RequestBody HashMap<String,String> body,@RequestParam  String privateKey) throws Exception{

        body.remove("sign");
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        log.info("base={}",baseString);
        resp.put("baseString",baseString);

        String sign = Utils.signRSA(baseString,privateKey);
        resp.put("sign",sign);

        return Utils.mapper.writeValueAsString(resp);
    }

    @PostMapping("/verifySign")
    public String verifySign(@RequestBody HashMap<String,String> body,@RequestParam  String publicKey) throws Exception{

        String sign = body.get("sign");
        body.remove("sign");
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        boolean flag = Utils.verifyRSA(baseString,sign,publicKey);
        log.info("base={},sign={},flag={}",baseString,sign,flag);

        if(!flag){
            resp.put("code",500);
            resp.put("success", flag);
        }
        return Utils.mapper.writeValueAsString(resp);
    }

    @PostMapping("/callback")
    public String callback(@RequestBody HashMap<String,String> body) throws Exception{
        //通过HashMap<String,String> 接受回调参数，不会造成浮点数据精度问题，从而导致签名计算不正确问题
        //或者通过Pojo 来接收回调参数，对应的浮点字段用 BigDecimal
        String sign = body.get("sign");
        body.remove("sign");
        String baseString = Utils.createBaseString1(body);

        Map<String,Object> resp = new HashMap<>();
        resp.put("code",200);
        resp.put("success",true);

        log.info("base={},sign={}",baseString,sign);

        boolean flag = Utils.verifyRSA(baseString,sign,"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBjEj/DylMlxONCDkkZQxh+woiD4goiG5WM+Ju3V2hmJpjpGCqXDClf4TLTymZMyM4GF0JL1euwgaacZ/pcxVHXpyGg8UstFUPrw7SStYURk4CLIWjuCrzZwALLGFQFNxQGFsXCR1WwpE08byw0asTWTL4VB9YlYRiV8huB/gcqwIDAQAB");
        if(!flag){
            resp.put("code",500);
            resp.put("success",false);
        }
        return Utils.mapper.writeValueAsString(resp);
    }

    @GetMapping("/genKey")
    public String genKey() throws Exception{
        return Utils.mapper.writeValueAsString(Utils.initKey());
    }
}
