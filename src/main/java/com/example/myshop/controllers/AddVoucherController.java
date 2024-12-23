package com.example.myshop.controllers;

import com.example.myshop.models.Category;
import com.example.myshop.models.Product;
import com.example.myshop.models.Voucher;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class AddVoucherController  implements Initializable {
    Voucher voucher;
    @FXML
    private Button addBtn;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField priceInput;
    private VoucherTabController voucherTabController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBtn.setOnAction(event -> {
            //them vao Database
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "INSERT INTO Vouchers(VoucherName, Price)\n" +
                    "VALUES ('" + voucher.getVoucherName() + "', " + voucher.getPrice() + ");";
            System.out.println(sql);
            if (connector.queryUpdate(sql)) {
                voucherTabController.loadVouchers();
                ((Node) (event.getSource())).getScene().getWindow().hide();
            }
        });
    }
    public void initParams(VoucherTabController voucherTabController){
        voucher = new Voucher();
        nameInput.textProperty().bindBidirectional(voucher.NameProperty());
        priceInput.textProperty().bindBidirectional(voucher.PriceProperty(), NumberFormat.getNumberInstance());
        this.voucherTabController = voucherTabController;


    }

}




