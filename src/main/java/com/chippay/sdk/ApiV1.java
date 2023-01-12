package com.chippay.sdk;

import com.chippay.sdk.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ApiV1 {
    private String secretKey;
    public ApiV1(String secretKey){
        this.secretKey = secretKey;
    }
    public String call(String url, Map<String,Object> params) throws Exception{

        Map<String,Object> origParams = new HashMap<>();
        origParams.putAll(params);
        params.put("secretKey", secretKey);
        String baseString = Utils.createBaseString(params);

        if (params.get("signType") != null && params.get("signType").equals(1)) {
            String sign = Utils.HMACSHA256(baseString,secretKey);
            origParams.put("sign",sign);
            return Utils.httpPost(url,origParams);
        }else{
            String sign = Utils.md5(baseString);
            origParams.put("sign",sign);
            return Utils.httpPost(url,origParams);
        }
    }
}
