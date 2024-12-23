package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Product;
import com.example.myshop.models.Voucher;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class VoucherTabController implements Initializable {
    @FXML
    private Button addBtn;
    @FXML
    private TableColumn<Voucher, String> idCol;
    @FXML
    private TableColumn<Voucher, String> nameCol;

    @FXML
    private TableColumn<Voucher, String> priceCol;
    @FXML
    private TableColumn<Voucher, Button> actionCol;
    @FXML
    private TableView<Voucher> tableView;
    ObservableList<Voucher> vouchers = FXCollections.observableArrayList();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setItems(vouchers);
        idCol.setCellValueFactory(param -> param.getValue().VoucherIDProperty().asString());
        nameCol.setCellValueFactory(param -> param.getValue().NameProperty());
        priceCol.setCellFactory(param -> {
            TableCell<Voucher, String> cell = new TableCell<Voucher, String>(){
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty){
                        int rowID = getIndex();
                        Voucher voucher = vouchers.get(rowID);

                        Locale locale = new Locale("vn", "VN");
                        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                        setText(currencyFormatter.format(voucher.getPrice()));
                    }
                    else {
                        setText(null);
                    }

                }
            };
            return cell;
        });
        actionCol.setCellFactory(param -> {
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("components/delete-btn-component.fxml"));
                final Button btn = fxmlLoader.load();
                TableCell<Voucher, Button> cell = new TableCell<Voucher, Button>(){
                    @Override
                    public void updateItem(Button item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty){
                            btn.setOnAction(event -> {
                                int rowID = getIndex();
                                Voucher voucher = vouchers.get(rowID);
                                System.out.println("Xoa voucher" + voucher.getVoucherName());

                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + voucher.getVoucherName() + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
                                alert.showAndWait();

                                if (alert.getResult() == ButtonType.YES) {
                                    deleteVoucherByID(voucher.getVoucherID());
                                }
                            });

                            setGraphic(btn);
                        }
                        else {
                            setGraphic(null);
                        }

                    }
                };
                return cell;
            }
            catch (Exception ex){
                System.out.println(ex);
            }
            return null;

        });
        loadVouchers();
        addBtn.setOnAction(event -> {
                    //Mo window nhap san phamStage stage = new Stage;
                    try{
                        Stage stage = new Stage();
                        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("windows/add-voucher-window.fxml"));
                        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
                        AddVoucherController controller = fxmlLoader.getController();
                        controller.initParams(this);
                        stage.setTitle("Thêm Sản Phẩm!");
                        stage.setScene(scene);
                        stage.show();
                    }catch (Exception ex){
                        System.out.println(ex);
                    }

                }
        );

    }
    void deleteVoucherByID(int voucherID){
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            String sql =  "delete from vouchers \n" +
                    "WHERE voucherID = " + voucherID;
            System.out.println(sql);
            mySQLConnector.queryUpdate(sql);
            loadVouchers();
        }catch (Exception ex){
            System.out.println(ex);
        }
    }
    void loadVouchers(){
        vouchers.clear();
        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        try{
            ResultSet resultSet = mySQLConnector.queryResults("select * from vouchers");
            while(resultSet.next()){
                int voucherID = resultSet.getInt(1);
                String voucherName = resultSet.getString(2);
                long price = resultSet.getLong(3);

                Voucher voucher = new Voucher(voucherID, voucherName, price);
                vouchers.add(voucher);
                System.out.println(MessageFormat.format("{0} - {1} - {2} ", voucherID, voucherName,price));
            }
        }catch (Exception ex){
            System.out.println(ex);
        }
    }

}
