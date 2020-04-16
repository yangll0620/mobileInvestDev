package com.example.smartinvest;

import java.util.Date;

public class Transaction {
    private String transFundSymbol;
    private String transFundName;
    private Date transDate;
    private float transPrice, transAmount;
    private int transShares;


    public Transaction()
    {
        this.transFundSymbol = "";
        this.transFundName = "";
    }

    public Transaction(String fundSymbol, String fundName, Date date, float price, int shares, float amount)
    {
        this.transFundSymbol = fundSymbol;
        this.transFundName = fundName;
        this.transDate = date;
        this.transPrice = price;
        this.transShares = shares;
        this.transAmount = amount;
    }



    public String getTransFundSymbol() { return this.transFundSymbol; }
    public void setTransFundSymbol(String transFundSymbol) { this.transFundSymbol = transFundSymbol; }

    public String getTransFundName()
    {
        return this.transFundName;
    }
    public void setTransFundName(String transFundName)
    {
        this.transFundName = transFundName;
    }


    public Date getTransDate() { return this.transDate; }
    public void setTransDate(Date date) { this.transDate = date; }

    public int getTransShares()
    {
        return this.transShares;
    }
    public void setTransShares(int shares)
    {
        this.transShares = shares;
    }

    public float getTransPrice()
    {
        return this.transPrice;
    }
    public void setTransPrice(float price)
    {
        this.transPrice = price;
    }

    public float getTransAmount()
    {
        return this.transAmount;
    }
    public void setTransAmount(float amount)
    {
        this.transAmount = amount;
    }
}