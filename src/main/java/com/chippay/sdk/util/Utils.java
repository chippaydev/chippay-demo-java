package com.chippay.sdk.util;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

public class Utils {

    public static ObjectMapper mapper = new ObjectMapper();

    private final static int CONNECT_TIMEOUT = 6000;
    private final static int SOCKET_TIMEOUT = 30000;
    private final static int REQUEST_TIMEOUT = 30000;


    public static String HMACSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    public static void removeNull(Map<String, Object> params){

        Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            if (ObjectUtil.isEmpty(entry.getValue())){
                it.remove();
            }
        }
    }

    public static String createBaseString(Map<String, Object> params) {
        removeNull(params);
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String baseString = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = params.get(key);
            if (i == keys.size() - 1) {
                baseString = baseString + key + "=" + value;
            } else {
                baseString = baseString + key + "=" + value + "&";
            }
        }
        return baseString;
    }

    public static void removeNull1(Map<String, String> params){

        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (ObjectUtil.isEmpty(entry.getValue())){
                it.remove();
            }
        }
    }

    public static String createBaseString1(Map<String, String> params) {
        removeNull1(params);
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String baseString = "";
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {
                baseString = baseString + key + "=" + value;
            } else {
                baseString = baseString + key + "=" + value + "&";
            }
        }
        return baseString;
    }

    public static String md5(String buffer) throws Exception {

        char[] hexDigest = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(buffer.getBytes("UTF-8"));
        byte[] data = md.digest();
        char[] str = new char[32];
        int k = 0;

        for(int i = 0; i < 16; ++i) {
            byte b = data[i];
            str[k++] = hexDigest[b >>> 4 & 15];
            str[k++] = hexDigest[b & 15];
        }

        return new String(str);

    }

    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static String signRSA(String data, String priKeyStr) throws Exception {

        byte[] priKey = Base64.decodeBase64(priKeyStr);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(priKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64String(signature.sign());
    }

    public static boolean verifyRSA(String requestData, String signature, String key) throws Exception {
        boolean verifySignSuccess = false;

        byte[] keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        Signature verifySign = Signature.getInstance("SHA256withRSA");
        verifySign.initVerify(publicKey);
        verifySign.update(requestData.getBytes());
        verifySignSuccess = verifySign.verify(Base64.decodeBase64(signature));
        return verifySignSuccess;
    }


    public static Map<String, String> initKey() throws Exception {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        SecureRandom secrand = new SecureRandom();
        // 下面setSeed，是額外加入一些熵（多混進一些亂數），可以使用也可以拿掉這行．是安全加分行為，但不是必要，因為SecureRandom 本身已經是安全的
        // byte[] manualSeed = new byte[32];
        // new SecureRandom().nextBytes(manualSeed); // 手動生一組種子
        // secrand.setSeed(manualSeed); // 混進去
        keygen.initialize(1024, secrand);
        KeyPair keys = keygen.genKeyPair();
        byte[] pub_key = keys.getPublic().getEncoded();
        String publicKeyString = Base64.encodeBase64String(pub_key);
        byte[] pri_key = keys.getPrivate().getEncoded();
        String privateKeyString = Base64.encodeBase64String(pri_key);
        Map<String, String> keyPairMap = new HashMap();
        keyPairMap.put("publicKey", publicKeyString);
        keyPairMap.put("privateKey", privateKeyString);
        return keyPairMap;
    }

    public static String httpPost(String url,Map<String, Object> params) throws Exception{

        CloseableHttpClient client = HttpClients.createDefault();

        HttpPost request = new HttpPost(url);
        request.setHeader("Content-Type","application/json");
        StringEntity entity = new StringEntity(mapper.writeValueAsString(params), "UTF-8");
        request.setEntity(entity);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();


        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        }else{
            throw new Exception("http call failure,code="+response.getStatusLine().getStatusCode());
        }
    }

    public static String httpGet(String url) throws Exception{

        CloseableHttpClient client = HttpClients.createDefault();

        HttpGet request = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

        HttpResponse response = client.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(response.getEntity());
        }else{
            throw new Exception("http call failure,code="+response.getStatusLine().getStatusCode());
        }
    }
}
