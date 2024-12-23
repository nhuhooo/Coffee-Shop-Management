package com.example.myshop.models;

import javafx.beans.property.*;

import java.text.MessageFormat;

public class Voucher {
    IntegerProperty VoucherID;
    StringProperty VoucherName;
    LongProperty Price;

    public Voucher() {
        this.VoucherID = new SimpleIntegerProperty();
        this.VoucherName = new SimpleStringProperty();
        this.Price = new SimpleLongProperty();
    }
    public Voucher(int voucherID, String voucherName,long price){
        this.VoucherID = new SimpleIntegerProperty(voucherID);
        this.VoucherName = new SimpleStringProperty(voucherName);
        this.Price = new SimpleLongProperty(price);
    }
    public int getVoucherID() {
        return VoucherID.get();
    }
    public long getPrice() {
        return Price.get();
    }
    public String getVoucherName() {
        return VoucherName.get();
    }
    public StringProperty NameProperty(){
        return VoucherName;
    }
    public LongProperty PriceProperty(){
        return Price;
    }
    public IntegerProperty VoucherIDProperty(){return VoucherID;}
    public void setVoucherName(String productName) {
        this.VoucherName.set(productName);
    }
    public void setVoucherID(int productID) {
        this.VoucherID.set(getVoucherID());
    }
    @Override
    public String toString(){
        int voucherID = getVoucherID();
        String voucherName = getVoucherName();
        long price = getPrice();
        return MessageFormat.format("{0} - {1} - {2}", voucherID, voucherName, price);
    }
    public Voucher clone(){
        return new Voucher(getVoucherID(), getVoucherName(), getPrice());
    }
}




