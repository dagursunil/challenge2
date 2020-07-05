package com.journi.challenge.controllers;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Product;
import com.journi.challenge.models.PurchaseStats;
import com.journi.challenge.repositories.ProductsRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * This class is implementation for product end points .
 * @author sdagar
 *
 */
@RestController
public class ProductsController {

    @Inject
    private ProductsRepository productsRepository;
    @Inject
    private CurrencyConverter currencyConverter;

    /**
     * This method will return all available product list.
     * @param countryCode
     * @return response entity.
     */
    @GetMapping("/products")
    public ResponseEntity getProdcuctsList(@RequestParam(name = "countryCode", defaultValue = "AT") String countryCode) {
    	
    	List<Product> products = productsRepository.list().stream().map(a -> new Product(a.getId(), a.getDescription(), 
    			currencyConverter.convertEurToCurrency(currencyConverter.getCurrencyForCountryCode(countryCode), a.getPrice()))).collect(Collectors.toList());
    	 return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
       
    }
    
   
    
}
