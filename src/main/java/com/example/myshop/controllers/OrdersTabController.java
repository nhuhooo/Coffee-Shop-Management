package com.example.myshop.controllers;
import com.example.myshop.Main;
import com.example.myshop.models.Order;
import com.example.myshop.models.OrderDetail;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class OrdersTabController implements Initializable {

    @FXML
    private TableColumn<Order, Button> actionCol;

    @FXML
    private Button addBtn;

    @FXML
    private TableColumn<Order, LocalDateTime> dateTimeCol;

    @FXML
    private TableColumn<Order, String> idCol;

    @FXML
    private TableColumn<Order, String> productsCol;

    @FXML
    private TableView<Order> tableView;

    @FXML
    private TableColumn<Order, String> totalPriceCol;
    @FXML
    private Button nextBtn;

    @FXML
    private Button prevBtn;
    @FXML
    private Label totalPageLabel;
    @FXML
    private Label currentPageLabel;
    @FXML
    private HBox datePickerHbox;

    @FXML
    private DatePicker fromDatePicker;

    @FXML
    private Button okBtn;
    @FXML
    private DatePicker toDatePicker;




    private int currentPage = 1;
    private int totalPage = 1;
    private int pageSize = 10;
    ObservableList<Order> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadOrders();

        int totalItems = countAllOrders();
        totalPage = (int) Math.ceil(1.0 * totalItems / pageSize);
        currentPage = 1;

        currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
        totalPageLabel.textProperty().bind(new SimpleIntegerProperty(totalPage).asString());
        prevBtn.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadOrders();
            }
        });
        nextBtn.setOnAction(event -> {
            if (currentPage < totalPage) {
                currentPage++;
                currentPageLabel.textProperty().bind(new SimpleIntegerProperty(currentPage).asString());
                loadOrders();
            }
        });

        tableView.setItems(orders);
        addBtn.setOnAction(event -> {
            //Mo window nhap san phamStage stage = new Stage;
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/create-order-window.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 800, 600);
                CreateOrderController controller = fxmlLoader.getController();
                controller.init(this);
                stage.setTitle("Tạo đơn hàng");
                stage.setScene(scene);
                stage.show();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        });

        idCol.setCellValueFactory(param -> param.getValue().OrderIDProperty().asString());

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
        actionCol.setCellFactory(param -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("components/delete-btn-component.fxml"));
                final Button btn = fxmlLoader.load();
                TableCell<Order, Button> cell = new TableCell<Order, Button>() {
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            btn.setOnAction(event -> {
                                int rowID = getIndex();
                                Order order = orders.get(rowID);

                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete Order " + order.getOrderID() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    deleteOrderByID(order.getOrderID());
                                }
                            });

                            setGraphic(btn);
                        } else {
                            setGraphic(null);
                        }

                    }
                };
                return cell;
            } catch (Exception ex) {
                System.out.println(ex);
            }
            return null;
        });
        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());
        okBtn.setOnAction(event -> {
            loadDate(fromDatePicker.getValue(), toDatePicker.getValue());
        });


    }
    void loadDate(LocalDate from, LocalDate to) {

        orders.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String sql = "select * from orders where orders.DateTime between '" + from.toString() + "'and'" + to.toString() + "'";
            ResultSet resultSet = mySQLConnector.queryResults(sql);
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
            nextBtn.setDisable(currentPage == totalPage);
            prevBtn.setDisable(currentPage == 1);

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }




    void deleteOrderByID(int OrderID) {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            String sql = "delete from orderdetails \n" +
                    "WHERE OrderID = " + OrderID;
            System.out.println(sql);
            mySQLConnector.queryUpdate(sql);
            loadOrders();
        } catch (Exception ex) {
            System.out.println(ex);
        }
        try {
            String sql = "delete from orders \n" +
                    "WHERE OrderID = " + OrderID;
            System.out.println(sql);
            mySQLConnector.queryUpdate(sql);
            loadOrders();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void loadOrders() {
        //load orders tu database
        orders.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from orders limit " + pageSize + " offset " + (currentPage - 1) * pageSize);
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
            nextBtn.setDisable(currentPage == totalPage);
            prevBtn.setDisable(currentPage == 1);

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }


    int countAllOrders() {
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select count(orderID) from orders");
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return 0;
    }


}


