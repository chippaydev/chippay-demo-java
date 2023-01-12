package com.chippay.sdk;
import com.chippay.sdk.util.Utils;

import java.util.HashMap;
import java.util.Map;

public class ApiV2 {
    private String privateKey;
    public ApiV2(String privateKey){
        this.privateKey = privateKey;
    }
    public String call(String url, Map<String,Object> params) throws Exception{

        Map<String,Object> origParams = new HashMap<>();
        origParams.putAll(params);

        String baseString = Utils.createBaseString(params);
        String sign = Utils.signRSA(baseString,privateKey);
        origParams.put("sign",sign);
        return Utils.httpPost(url,origParams);

    }
}
