package com.example.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ProductService  {
    private final String baseUrl = "http://localhost:8080/api/products";
    private final ObjectMapper objectMapper = new ObjectMapper();

    //GET
    public List<Product> getAllProducts()  throws IOException {
        URL url = new URL(baseUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        InputStream input = conn.getInputStream();
        return objectMapper.readValue(input, new TypeReference<List<Product>>(){});
    }

    //POST
    public void createProduct(Product product) throws IOException {
        sendWithBody("POST", product, baseUrl);
    }

    //PUT
    public void updateProduct(Product product) throws IOException {
        URL url = new URL(baseUrl + "/" + product.getId());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        // Convertimos el objeto Product en JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(product);

        // Enviamos el JSON al servidor
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Verificamos la respuesta del servidor
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new IOException("Error al actualizar producto. Código HTTP: " + responseCode);
        }
    }


    //DELETE
    public void deleteProduct(Long id) throws IOException {
        URL url = new URL(baseUrl + "/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.connect();
        conn.getResponseCode(); // Ejecutar
    }

    private void sendWithBody(String method, Product product, String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        objectMapper.writeValue(os, product);
        os.flush();
        os.close();

        conn.getResponseCode(); // Ejecutar
    }
}
