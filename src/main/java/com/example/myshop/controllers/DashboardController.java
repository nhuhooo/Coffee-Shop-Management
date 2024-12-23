package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DashboardController implements Initializable {


    @FXML
    private TableColumn<Product, String> idCol;


    @FXML
    private TableColumn<Product, String> nameCol;

    @FXML
    private TableColumn<Order, LocalDateTime> dateTimeCol;

    @FXML
    private TableColumn<Order, String> idCol1;

    @FXML
    private TableColumn<Order, String> productsCol;

    @FXML
    private TableView<Order> tableView1;

    @FXML
    private TableColumn<Order, String> totalPriceCol;

    @FXML
    private TableView<Product> tableView;

    @FXML
    private BarChart<String, Integer> BarChart;


    ObservableList<Order> orders = FXCollections.observableArrayList();
    ObservableList<Product> products = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Chart();
        tableView.setItems(products);
        tableView1.setItems(orders);

        idCol.setCellValueFactory(param -> param.getValue().ProductIDProperty().asString());
        nameCol.setCellValueFactory(param -> param.getValue().NameProperty());


        loadOrders();
        loadProducts();

        idCol1.setCellValueFactory(param -> param.getValue().OrderIDProperty().asString());

        totalPriceCol.setCellFactory(column -> {
            TableCell<Order, String> cell = new TableCell<>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        long amount = getTableView().getItems().get(getIndex()).getTotalPice();
                        Locale locale = new Locale("vn", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        setText(currencyFormatter.format(amount));
                    }
                }
            };

            return cell;
        });
        dateTimeCol.setCellFactory(column -> {
            TableCell<Order, LocalDateTime> cell = new TableCell<Order, LocalDateTime>() {

                DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

                @Override
                protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(format.format(getTableView().getItems().get(getIndex()).getDate()));
                    }
                }
            };

            return cell;
        });
        productsCol.setCellFactory(column -> {
            TableCell<Order, String> cell = new TableCell<Order, String>() {

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(getTableView().getItems().get(getIndex()).ProductsDisplay());
                    }
                }
            };

            return cell;
        });


    }

    void loadProducts() {
        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from products");
            while (resultSet.next()) {
                int productID = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                int categoryID = resultSet.getInt(3);
                String imagePath = resultSet.getString(4);
                int quantity = resultSet.getInt(5);
                long price = resultSet.getLong(6);

                Product product = new Product(productID, productName, null, imagePath, quantity, price);
                products.add(product);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void loadOrders() {
        //load orders tu database
        orders.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from orders order by dateTime DESC limit 6 ");
            while (resultSet.next()) {
                int orderID = resultSet.getInt(1);
                LocalDateTime dateTime = resultSet.getTimestamp(2).toLocalDateTime();

                long totalPrice = resultSet.getLong(3);
                ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
                ResultSet resultSet2 = mySQLConnector.queryResults("select *\n" +
                        "from orderdetails join products on orderdetails.ProductID = products.ProductID where orderID = " + orderID);
                while (resultSet2.next()) {
                    int productID = resultSet2.getInt(2);
                    int quantity = resultSet2.getInt(3);
                    String productName = resultSet2.getString(5);
                    int categoryID = resultSet2.getInt(6);
                    String imagePath = resultSet2.getString(7);
                    int inStock = resultSet2.getInt(8);
                    long price = resultSet2.getLong(9);

                    Product product = new Product(productID, productName, null, imagePath, inStock, price);

                    orderDetails.add(new OrderDetail(orderID, product, quantity));
                }

                Order order = new Order(orderID, orderDetails, dateTime, totalPrice);
                orders.add(order);
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }
    public void Chart() {

        try {

            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "SELECT ProductID,SUM(Quantity)FROM OrderDetails GROUP BY productid order by SUM(Quantity)  DESC limit 5;";
            ResultSet resultSet = connector.queryResults(sql);

            while (resultSet.next()) {
                XYChart.Series data = new XYChart.Series<String, Integer>();
                String ProductID = resultSet.getString(1);
                int Quantity = resultSet.getInt(2);

                BarChart.getData().add(data);

                data.getData().add(new XYChart.Data<>(ProductID, Quantity));


            }
            BarChart.setLegendVisible(false);


        } catch (Exception ex) {
            System.out.println(ex);
        }


    }


}


