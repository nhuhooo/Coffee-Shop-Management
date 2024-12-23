package com.example.myshop.controllers;

import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
public class StatisticController implements Initializable {
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private ComboBox<String> comboBox1;


    @FXML
    private HBox datePickerHbox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Button okBtn;

    @FXML
    private Label revenue;
    @FXML
    private Label order;
    @FXML
    private Label sold;


    @FXML
    private DatePicker toDatePicker;

    @FXML
    private BarChart<String, Integer> BarChart;
    @FXML
    private BarChart<String, Integer> BarChart1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRevenueAllTime();

        BarChart1.setVisible(false);
        BarChart.setVisible(true);
        Chart();
        Chart1();
        comboBox1.setItems(FXCollections.observableArrayList( "Date to Date","Month"));
        comboBox1.setOnAction(event -> {
            if (comboBox1.getSelectionModel().getSelectedIndex() == 0) {
                BarChart1.setVisible(false);
                BarChart.setVisible(true);
            } else {
                BarChart.setVisible(false);
                BarChart1.setVisible(true);

            }
        });

        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        datePickerHbox.setVisible(false);
        comboBox.setItems(FXCollections.observableArrayList("All", "Date to Date"));
        comboBox.setOnAction(event -> {
            if (comboBox.getSelectionModel().getSelectedIndex() == 0) {
                loadRevenueAllTime();
                datePickerHbox.setVisible(false);
            } else {
                datePickerHbox.setVisible(true);

            }
        });
        okBtn.setOnAction(event -> {
            loadRevenueFromDateToDate(fromDatePicker.getValue(), toDatePicker.getValue());
        });
        loadOrder();
        loadSold();


    }
    void loadOrder() {
        order.setText(getNum() +" ");
    }
    private long getNum() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT COUNT(OrderID) FROM Orders";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long order = resultSet.getLong(1);
                return order;
            }
        } catch (Exception ex) {

        }
        return 0;
    }
    void loadSold() {
        sold.setText(getNum1() +" ");
    }
    private long getNum1() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT SUM(Quantity) FROM OrderDetails";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long sold = resultSet.getLong(1);
                return sold;
            }
        } catch (Exception ex) {

        }
        return 0;
    }


    void loadRevenueFromDateToDate(LocalDate from, LocalDate to) {
        revenue.setText(getRevenueFromDatetoDate(from, to) + "");
    }

    void loadRevenueAllTime() {
        revenue.setText(getRevenue() + "");
    }

    private long getRevenue() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "select SUM(orders.TotalPrice) \n" +
                    "from orders";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long revenue = resultSet.getLong(1);
                return revenue;
            }
        } catch (Exception ex) {

        }
        return 0;
    }

    private long getRevenueFromDatetoDate(LocalDate from, LocalDate to) {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql =
                    "select SUM(orders.TotalPrice) \n" +
                            "from orders where orders.DateTime between '" + from.toString() + "'and'" + to.toString() + "'";
            ResultSet resultSet = connector.queryResults(sql);
            if (resultSet.next()) {
                long revenue = resultSet.getLong(1);
                return revenue;
            }
        } catch (Exception ex) {

        }
        return 0;
    }

    public void Chart() {

        try {

            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "select DATE_FORMAT(DateTime,'%d/%m/%Y') AS niceDate ,SUM(Orders.TotalPrice) from orders group by niceDate\n";
            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                XYChart.Series data = new XYChart.Series<String, Integer>();
                String Datetime = resultSet.getString(1);
                int TotalPrice = resultSet.getInt(2);

                BarChart.getData().add(data);

                data.getData().add(new XYChart.Data<>(Datetime, TotalPrice));


            }
            BarChart.setLegendVisible(false);


        } catch (Exception ex) {
            System.out.println(ex);
        }


    }

    public void Chart1() {
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "select DATE_FORMAT(DateTime,'%m') AS month ,SUM(Orders.TotalPrice) from orders group by month\n";
            ResultSet resultSet = connector.queryResults(sql);
            while (resultSet.next()) {
                XYChart.Series data = new XYChart.Series<String, Integer>();

                String Datetime = resultSet.getString(1);
                int TotalPrice = resultSet.getInt(2);
                BarChart1.getData().add(data);
                data.getData().add(new XYChart.Data<>(Datetime, TotalPrice));



            }
            BarChart1.setLegendVisible(false);



        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
}


