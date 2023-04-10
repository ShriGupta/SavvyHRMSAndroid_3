package com.savvy.hrmsnewapp.room_database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class AdvanceModel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "currency")
    private String currency;

    @ColumnInfo(name = "paymode")
    private String paymode;

    @ColumnInfo(name = "remarks")
    private String remarks;

    @ColumnInfo(name = "paymodeid")
    private String paymodeid;

    @ColumnInfo(name = "currencyid")
    private String currencyid;


    public void setCurrencyid(String currencyid) {
        this.currencyid = currencyid;
    }

    public void setPaymodeid(String paymodeid) {
        this.paymodeid = paymodeid;
    }


    public String getCurrencyid() {
        return currencyid;
    }

    public String getPaymodeid() {
        return paymodeid;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getId() {
        return id;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymode() {
        return paymode;
    }

    public String getRemarks() {
        return remarks;
    }


}
