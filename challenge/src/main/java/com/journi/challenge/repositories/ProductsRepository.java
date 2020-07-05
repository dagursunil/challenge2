package com.journi.challenge.repositories;

import com.journi.challenge.models.Product;
import org.springframework.stereotype.Component;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Named
@Singleton
public class ProductsRepository {
    private List<Product> allProducts = new ArrayList<>();
    {
        allProducts.add(new Product("photobook-square-soft-cover", "Photobook Square with Soft Cover", 25.0));
        allProducts.add(new Product("photobook-square-hard-cover", "Photobook Square with Hard Cover", 30.0));
        allProducts.add(new Product("photobook-landscape-soft-cover", "Photobook Landscape with Soft Cover", 35.0));
        allProducts.add(new Product("photobook-landscape-hard-cover", "Photobook Landscape with Hard Cover", 45.0));
    }

    public List<Product> list() {
        return allProducts;
    }
}
