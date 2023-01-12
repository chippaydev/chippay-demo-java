package com.chippay.demo.common.enums;

public enum ExceptionEnum {
    //系统异常
    SUCCESS(200, "成功"),
    UNKNOWN_ERROR(-1, "未知错误"),
    SYSTEM_ERROR(-2, "系统异常"),

    //错误
    QUERY_PRICE(40000, "查询价格异常"),
    QUERY_LIMIT(40001, "查询支付方式交易限额异常"),
    MERCHANT_ACCOUNT(40002, "查询支付方式交易限额异常"),
    PRICE_COIN_NULL(40003,"货币标识不能为空"),
    QUERY_USER_TRADE_ERROR(40004, "查询用户交易情况异常"),
    QUERY_ORDER(40005, "查询订单异常"),
    SIGN_ERROR(40006, "签名异常"),
    BUY_ORDER_ERROR(40007, "下买单异常"),
    SELL_ORDER_ERROR(40008, "下卖单异常"),
    ;

    private Integer code;

    private String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    public static ExceptionEnum getCode(String param){
        for(ExceptionEnum exceptionEnum : ExceptionEnum.values()){
            String variable = exceptionEnum.toString().replaceAll("_", "");
            if(param.equalsIgnoreCase(variable)){
                return exceptionEnum;
            }
        }
        return ExceptionEnum.SYSTEM_ERROR;
    }

}