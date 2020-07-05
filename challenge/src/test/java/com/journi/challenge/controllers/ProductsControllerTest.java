package com.journi.challenge.controllers;

import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.journi.challenge.models.Product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class ProductsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductsController controller;

    @Test
    public void shouldListProductsWithCurrencyCodeAndConvertedPriceDefault() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", IsEqual.equalTo(4)));
    }

    @Test
    public void shouldListProductsWithCurrencyCodeAndConvertedPriceBR() throws Exception {
        mockMvc.perform(get("/products?countryCode=BR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", IsEqual.equalTo(4)));
    }

    @Test
    public void shouldListProductsWithCurrencyCodeEURWhenCountryCodeNonSupported() throws Exception {
        mockMvc.perform(get("/products?countryCode=JP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", IsEqual.equalTo(4)));
                
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Test void testgetProdcuctsList() {
    	ResponseEntity response=controller.getProdcuctsList("HUF");
    	List<Product> productList=(List<Product>) response.getBody();
    	assertNotNull(productList);
    	assertEquals(4, productList.size());
    	
    }
}
