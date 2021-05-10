package com.github.menglim.mutils.model;

import lombok.Data;

@Data
public class CurrencyModel {

    private int digitalCode;

    private String currencyCode;

    private String currencyName;

    private String countryName;

    private String symbol;

    public CurrencyModel(int digitalCode, String currencyCode, String currencyName, String countryName, String symbol) {
        this.digitalCode = digitalCode;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.countryName = countryName;
        this.symbol = symbol;
    }
}
