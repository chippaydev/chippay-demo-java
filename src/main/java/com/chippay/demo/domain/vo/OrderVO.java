package com.chippay.demo.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author peter
 * @create 2020-07-08-18:28
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVO implements Serializable {

    public interface AddOrder { }

    public interface GetOrderInfo { }

    @NotNull(message = "商户id不能为空", groups = {GetOrderInfo.class})
    private Long companyId;

    @NotBlank(message = "商户订单号不能为空", groups = {GetOrderInfo.class})
    private String companyOrderNum;

    private Long companyUserId;

    private Long userId;

    private Integer kyc;

    private String username;

    private String areaCode;

    private String phone;

    private Integer orderType;

    private Integer idCardType;

    private String idCardNum;

    private String payCardNo;

    private String payCardBank;

    private String payCardBranch;

    private Integer orderPayChannel;

    private String orderNo;

    private String coinSign;

    private String payCoinSign;

    private BigDecimal coinSum;

    private BigDecimal unitPrice;

    private BigDecimal coinPrice;

    private BigDecimal displayAmount;

    private BigDecimal fee;

    private Integer mode = 1;

    private Long traderId;

    private Long traderUserId;

    private Long traderPaymentId;

    private String orderTime;

    private String syncUrl;

    private String asyncUrl;
    @NotBlank(message = "签名不能为空", groups = {AddOrder.class, GetOrderInfo.class})
    private String sign;

}

