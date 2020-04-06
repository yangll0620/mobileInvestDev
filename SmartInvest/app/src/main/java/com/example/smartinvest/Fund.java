package com.example.smartinvest;

public class Fund {
    private String fund_Symobol;
    private String fund_Name;

    public Fund(String fund_Symobol)
    {
        this.fund_Symobol = fund_Symobol;
    }

    public Fund(String fund_Symobol, String fund_Name)
    {
        this.fund_Symobol = fund_Symobol;
        this.fund_Name = fund_Name;
    }

    public String getFundSymbol()
    {
        return this.fund_Symobol;
    }

    public String getFundName()
    {
        return this.fund_Name;
    }
}

