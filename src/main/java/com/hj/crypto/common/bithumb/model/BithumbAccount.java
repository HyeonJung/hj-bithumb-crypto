package com.hj.crypto.common.bithumb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BithumbAccount {

    private String currency;          // 예: ETH, XRP, KRW
    private String balance;           // 잔고
    private String locked;            // 거래 중인 수량
    @JsonProperty("avg_buy_price")
    private String avgBuyPrice;       // 평균 매수가
    @JsonProperty("avg_buy_price_modified")
    private boolean avgBuyPriceModified; // 수정 여부
    @JsonProperty("unit_currency")
    private String unitCurrency;      // 기준 통화 (KRW)
}
