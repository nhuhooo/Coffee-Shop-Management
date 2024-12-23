package com.example.myshop.controllers;

import com.example.myshop.Main;
import com.example.myshop.models.Category;
import com.example.myshop.models.Product;
import com.example.myshop.utils.MySQLConnector;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class AddProductController implements Initializable {
    Product product;
    ObservableList<Category> categories;
    @FXML
    private Button addBtn;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button selectImageButton;

    //luu context cua man hinh danh sach san pham de update
    private ProductsTabController productsTabController;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addBtn.setOnAction(event -> {
            //them vao Database
            MySQLConnector connector = MySQLConnector.getInstance();
            String sql = "INSERT INTO Products(ProductName, CategoryID, ImagePath, Quantity, Price, isActive)\n" +
                    "VALUES ('" +product.getProductName() + "', "+product.getCategory().getCategoryID()+", '"+ product.getImagePath() +"', "+product.getQuantity()+", " +product.getPrice() +",true);";
            System.out.println(sql);
            if (connector.queryUpdate(sql)){
                productsTabController.loadProducts();
                ((Node)(event.getSource())).getScene().getWindow().hide();
            }

        });
        selectImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter imageFilter
                    = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
            fileChooser.getExtensionFilters().add(imageFilter);
            fileChooser.setTitle("Select a File");

            // Show the file dialog to pick a file
            File selectedFile = fileChooser.showOpenDialog( (Stage)((Node)event.getSource()).getScene().getWindow());
            if (selectedFile != null) {
                String folder = Main.class.getResource("pic/").getPath();
                folder = "/" + folder.substring(1);
                try {
                    String fileName = selectedFile.getPath().substring(selectedFile.getPath().lastIndexOf("/"));
                    System.out.println(fileName);
                    product.setImagePath("pic" + fileName);
                    imageView.setImage(new Image(Main.class.getResource("pic"+fileName).toExternalForm()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void initParams(ProductsTabController productsTabController, ObservableList<Category> categories){
        product = new Product();
        nameInput.textProperty().bindBidirectional(product.NameProperty());
        priceInput.textProperty().bindBidirectional(product.PriceProperty(), NumberFormat.getNumberInstance());
        quantityInput.textProperty().bindBidirectional(product.QuantityProperty(), NumberFormat.getNumberInstance());

        this.productsTabController = productsTabController;
        this.categories = categories;
        categoryComboBox.setItems(categories);
        categoryComboBox.valueProperty().bindBidirectional(product.CategoryProperty());

    }

}

