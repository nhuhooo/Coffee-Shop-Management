package com.example.myshop.models;

import javafx.beans.property.*;

import java.text.MessageFormat;

public class Product {
    IntegerProperty ProductID;
    StringProperty ProductName;
    ObjectProperty<Category> category;
    StringProperty ImagePath;
    IntegerProperty Quantity;
    LongProperty Price;

    public Product(){
        this.ProductID = new SimpleIntegerProperty();
        this.ProductName = new SimpleStringProperty();
        this.ImagePath = new SimpleStringProperty();
        this.Quantity = new SimpleIntegerProperty();
        this.Price = new SimpleLongProperty();
        this.category = new SimpleObjectProperty<>();
    }
    public Product(int productID, String productName, Category category, String imagePath, int quantity, long price){
        this.ProductID = new SimpleIntegerProperty(productID);
        this.ProductName = new SimpleStringProperty(productName);
        this.category = new SimpleObjectProperty<>(category);
        this.ImagePath = new SimpleStringProperty(imagePath);
        this.Quantity = new SimpleIntegerProperty(quantity);
        this.Price = new SimpleLongProperty(price);
    }
    public int getProductID() {
        return ProductID.get();
    }

    public Category getCategory() {
        return category.get();
    }

    public int getQuantity() {
        return Quantity.get();
    }

    public long getPrice() {
        return Price.get();
    }

    public String getImagePath() {
        return ImagePath.get();
    }

    public String getProductName() {
        return ProductName.get();
    }
    public StringProperty NameProperty(){
        return ProductName;
    }
    public StringProperty ImagePathProperty(){
        return ImagePath;
    }
    public IntegerProperty QuantityProperty(){
        return Quantity;
    }
    public LongProperty PriceProperty(){
        return Price;
    }
    public IntegerProperty ProductIDProperty(){return ProductID;}
    public ObjectProperty<Category> CategoryProperty(){return category;}

    public void setImagePath(String imagePath) {
        this.ImagePath.set(imagePath);
    }

    public void setProductName(String productName) {
        this.ProductName.set(productName);
    }

    public void setCategory(Category category) {
        this.category.set(category);
    }

    public void setQuantity(int quantity) {
        this.Quantity.set(quantity);
    }

    public void setProductID(int productID) {
        this.ProductID.set(productID);
    }


    @Override
    public String toString(){
        int productID = getProductID();
        String productName = getProductName();
        String imagePath = getImagePath();
        int quantity = getQuantity();
        long price = getPrice();
        return MessageFormat.format("{0} - {1} - {2} - {3} - {4}", productID, productName, imagePath, quantity, price);
    }
    public Product clone(){
        return new Product(getProductID(), getProductName(), getCategory(), getImagePath(), getQuantity(), getPrice());
    }
}
