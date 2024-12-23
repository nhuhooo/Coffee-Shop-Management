package com.example.myshop.controllers;

import com.example.myshop.models.*;
import com.example.myshop.utils.MySQLConnector;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;


public class CreateOrderController implements Initializable {
    @FXML
    private Button cancelBtn;

    @FXML
    private Label dateTime;
    @FXML
    private ComboBox<String> choose;

    @FXML
    private TableColumn<Product, String> idCol;

    @FXML
    private TableColumn<Product, String> nameCol;

    @FXML
    private TableColumn<Product, String> priceCol;

    @FXML
    private TableColumn<Product, String> quantityCol;

    @FXML
    private ListView<OrderDetail> listView;

    @FXML
    private Button okBtn;
    @FXML
    private Button okName;


    @FXML
    private TableView<Product> tableView;

    @FXML
    private Label totalPrice;
    @FXML
    private Label voucherprice;
    @FXML
    private Label MINUS;
    @FXML
    private Label MINUS1;
    @FXML
    private Label totalPrice2;
    @FXML
    private TextField nameInput;


    Order order;

    ObservableList<Product> products = FXCollections.observableArrayList();
    ObservableList<OrderDetail> orderDetails = FXCollections.observableArrayList();
    OrdersTabController ordersTabController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadProducts();
        choose.setItems(FXCollections.observableArrayList("All", "Coffee", "Tea&Juice", "Dessert"));
        choose.setOnAction(event -> {
            if (choose.getSelectionModel().getSelectedIndex() == 0) {
                loadProducts();
            }
            if (choose.getSelectionModel().getSelectedIndex() == 1) {
                loadCoffees();
            }
            if (choose.getSelectionModel().getSelectedIndex() == 2) {
                loadTea();
            }
            if (choose.getSelectionModel().getSelectedIndex() == 3) {
                loadDessert();
            }
        });
        order = new Order();
        tableView.setItems(products);
        idCol.setCellValueFactory(param -> param.getValue().ProductIDProperty().asString());
        nameCol.setCellValueFactory(param -> param.getValue().NameProperty());
        priceCol.setCellFactory(param -> {
            TableCell<Product, String> cell = new TableCell<Product, String>() {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        int rowID = getIndex();
                        Product product = products.get(rowID);
                        setText(String.valueOf(product.getPrice()));
                    } else {
                        setText(null);
                    }
                }
            };
            return cell;
        });
        quantityCol.setCellValueFactory(param -> param.getValue().QuantityProperty().asString());
        dateTime.textProperty().bind(Bindings.createStringBinding(() -> {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            return dtf.format(order.getDate());
        }, order.DateProperty()));

        totalPrice.textProperty().bind(Bindings.createStringBinding(() -> {
            return String.valueOf(order.getTotalPice());
        }, order.TotalPriceProperty()));
        listView.setItems(orderDetails);
        listView.setCellFactory(param -> new ListViewItemController(orderDetails, order));
        tableView.setOnMouseClicked(event -> {
            Product product = tableView.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) //Checking double click
            {

                if (product.getQuantity() == 0)
                    return;

                OrderDetail orderDetail = findProductInListView(product, orderDetails);
                product.setQuantity(product.getQuantity() - 1);
                if (orderDetail == null)
                    orderDetails.add(new OrderDetail(product, 1));
                else {
                    orderDetail.increaseQuantity();
                }
                order.UpdateOrderDetail(orderDetails);
            }
        });
        cancelBtn.setOnAction(event -> {
            Stage stage = (Stage) cancelBtn.getScene().getWindow();
            stage.close();
        });
        okBtn.setOnAction(event -> {
            //B1 tao don hang trong database
            int newOrderID = createOrderInDatabase();
            createOrderDetailInDatabase(newOrderID);
            //B2 tao cac OrderDetails trong database
            //load lai danh sach orders o Order Tab
            ordersTabController.loadOrders();

            Stage stage = (Stage) okBtn.getScene().getWindow();
            stage.close();

        });
        voucherprice.setText("0");
        MINUS.setText("- đ");
        MINUS1.setText(" đ");
        okName.setOnAction(event -> {
            okAction();
            int i = Integer.parseInt(voucherprice.getText());
            int t = Integer.parseInt(totalPrice.getText());
            int a = t-i;
            String total = String.valueOf(a);
            totalPrice2.setText(total);
        });
        nameInput.setOnAction(event -> {
            textField();
        });


    }
    public void okAction(){
        try{
            String voucherName = nameInput.getText();
            if (voucherName.length() > 0){
                String sql = "SELECT * FROM vouchers WHERE vouchername ='" + voucherName +"'";
                MySQLConnector mySQLConnector = MySQLConnector.getInstance();
                ResultSet resultSet = mySQLConnector.queryResults(sql);
                if (resultSet.next()) {
                    String price = resultSet.getString(3);
                    voucherprice.setText(price);
                }

            }
            else voucherprice.setText("0");

        }catch (Exception ex){}

    }
    private void textField() {
        if (nameInput.getText().length()==0 ){
            voucherprice.setText("");
        }
    }

    OrderDetail findProductInListView(Product product, ObservableList<OrderDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            if (product.getProductID() == list.get(i).getProduct().getProductID())
                return list.get(i);
        }
        return null;
    }

    public void init(OrdersTabController ordersTabController) {
        this.ordersTabController = ordersTabController;
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

    void loadCoffees() {
        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from products where CategoryID=1");
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

    void loadTea() {
        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from products where CategoryID=2");
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

    void loadDessert() {
        products.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try {
            ResultSet resultSet = mySQLConnector.queryResults("select * from products where CategoryID=3");
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

    int createOrderInDatabase() {
        //them vao Database
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "INSERT INTO Orders(DateTime, TotalPrice)\n" +
                    "VALUES ('" + order.getDate() + "', " + totalPrice2.getText() + ");";
            System.out.println(sql);
            if (connector.queryUpdate(sql)) {
                sql = "SELECT LAST_INSERT_ID()";
                ResultSet resultSet = connector.queryResults(sql);
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return -1;
    }

    void createOrderDetailInDatabase(int orderID) {
        //them vao Database
        try {
            MySQLConnector connector = MySQLConnector.getInstance();
            for (int i = 0; i < orderDetails.size(); i++) {
                OrderDetail orderDetail = orderDetails.get(i);
                String sql = "INSERT INTO OrderDetails(OrderID, ProductID, Quantity)\n" +
                        "VALUES (" + orderID + ", " + orderDetail.getProduct().getProductID() + ", " + orderDetail.getQuantity() + ");";
                connector.queryUpdate(sql);
                //cap nhat so luong ton kho cua product
                updateInStockOfProduct(orderDetail.getProduct().getProductID(), orderDetail.getProduct().getQuantity() - orderDetail.getQuantity());
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    void updateInStockOfProduct(int productID, int amount) {
        MySQLConnector connector = MySQLConnector.getInstance();
        String sql = "UPDATE products\n" +
                "SET Quantity = " + amount + "\n" +
                "WHERE productID = " + productID;
        connector.queryUpdate(sql);
    }
}




