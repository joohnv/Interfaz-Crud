package com.example.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApiAplication extends Application {
    private final ProductService service = new ProductService();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private TableView<Product> table;

    @Override
    public void start(Stage stage) {
        table = createTableView();

        Button loadButton = new Button("Cargar");
        Button addButton = new Button("Añadir");
        Button editButton = new Button("Editar");
        Button deleteButton = new Button("Eliminar");

        loadButton.setOnAction(e -> loadProducts());
        addButton.setOnAction(e -> showProductForm(null));
        editButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) showProductForm(selected);
        });
        deleteButton.setOnAction(e -> {
            Product selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) deleteProduct(selected);
        });

        HBox buttons = new HBox(10, loadButton, addButton, editButton, deleteButton);
        VBox layout = new VBox(10, table, buttons);
        layout.setPadding(new javafx.geometry.Insets(10));

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Gestión de Coches");
        stage.show();

        loadProducts();
    }

    private TableView<Product> createTableView() {
        TableView<Product> table = new TableView<>();
        table.setItems(productList);

        TableColumn<Product, Long> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getId()).asObject());

        TableColumn<Product, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNombre()));

        TableColumn<Product, Double> precioCol = new TableColumn<>("Precio");
        precioCol.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrecio()).asObject());

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()).asObject());

        table.getColumns().addAll(idCol, nombreCol, precioCol, stockCol);
        return table;
    }

    private void loadProducts() {
        try {
            productList.setAll(service.getAllProducts());
        } catch (Exception e) {
            showAlert("Error", "No se pudieron cargar los coches.");
            e.printStackTrace();
        }
    }

    private void showProductForm(Product existing) {
        Stage form = new Stage();
        TextField nombreField = new TextField();
        TextField precioField = new TextField();
        TextField stockField = new TextField();

        if (existing != null) {
            nombreField.setText(existing.getNombre());
            precioField.setText(String.valueOf(existing.getPrecio()));
            stockField.setText(String.valueOf(existing.getStock()));
        }

        Button save = new Button("Guardar");
        save.setOnAction(e -> {
            try {
                Product p = existing != null ? existing : new Product();
                p.setNombre(nombreField.getText());
                p.setPrecio(Double.parseDouble(precioField.getText()));
                p.setStock(Integer.parseInt(stockField.getText()));

                if (existing != null) {
                    p.setId(existing.getId()); // Corrección: conservar el ID original
                    System.out.println("Editando producto con ID: " + p.getId());
                    service.updateProduct(p);
                } else {
                    service.createProduct(p);
                }

                loadProducts();
                form.close();
            } catch (Exception ex) {
                showAlert("Error", "Error al guardar el coche.");
                ex.printStackTrace();
            }
        });

        VBox layout = new VBox(10,
                new Label("Nombre:"), nombreField,
                new Label("Precio:"), precioField,
                new Label("Stock:"), stockField,
                save
        );
        layout.setPadding(new javafx.geometry.Insets(10));
        form.setScene(new Scene(layout));
        form.setTitle(existing != null ? "Editar Coche" : "Nuevo Coche");
        form.show();
    }

    private void deleteProduct(Product product) {
        try {
            service.deleteProduct(product.getId());
            loadProducts();
        } catch (Exception e) {
            showAlert("Error", "No se pudo eliminar el coche.");
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR, content, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
