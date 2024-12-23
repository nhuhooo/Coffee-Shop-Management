package com.example.myshop.models;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;

public class Order {
    ObservableList<OrderDetail> list = FXCollections.observableArrayList();;
    ObjectProperty<LocalDateTime> dateTime;
    LongProperty totalPice;
    IntegerProperty orderID;
    public Order(){
        dateTime = new SimpleObjectProperty<>(LocalDateTime.now());
        totalPice = new SimpleLongProperty();
        orderID = new SimpleIntegerProperty();
    }
    public Order(int orderID, ObservableList<OrderDetail> list, LocalDateTime dateTime,long totalPrice ){

        this.list.addAll(list);
        this.dateTime = new SimpleObjectProperty<>(dateTime);
        this.totalPice = new SimpleLongProperty(totalPrice);
        this.orderID = new SimpleIntegerProperty(orderID);
    }

    public ObservableList<OrderDetail> getList() {
        return list;
    }

    public LocalDateTime getDate() {
        return dateTime.get();
    }

    public long getTotalPice() {
        return totalPice.get();
    }

    public int getOrderID() {
        return orderID.get();
    }

    public void setDateTime(LocalDateTime date) {
        this.dateTime.set(date);
    }

    public void setList(ObservableList<OrderDetail> list) {
        this.list = list;
    }

    public void setTotalPice(long totalPice) {
        this.totalPice.set(totalPice);
    }

    public void setOrderID(int orderID) {
        this.orderID.set(orderID);
    }
    public IntegerProperty OrderIDProperty(){
        return orderID;
    }

    public LongProperty TotalPriceProperty(){
        return totalPice;
    }
    public ObjectProperty<LocalDateTime> DateProperty(){
        return dateTime;
    }
    public void UpdateOrderDetail(ObservableList<OrderDetail> list){
        this.list.addAll(list);

        //update lai tong gia cua don hang
        long newTotalPrice = 0;
        for(int i = 0; i < list.size(); i++){
            newTotalPrice += list.get(i).getProduct().getPrice() * list.get(i).getQuantity();
        }
        setTotalPice(newTotalPrice);
    }
    public String ProductsDisplay(){
        String res = "";
        for(int i = 0; i < list.size();i++){
            res += list.get(i).toString() + ", ";
        }
        if (!res.equals("")){
            res =  res.substring(0, res.length() - 2);
        }
        return res;
    }
}