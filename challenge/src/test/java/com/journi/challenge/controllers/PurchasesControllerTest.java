package com.journi.challenge.controllers;

import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseStats;
import com.journi.challenge.repositories.PurchasesRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PurchasesControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PurchasesController purchasesController;
    @Autowired
    private PurchasesRepository purchasesRepository;

    private String getPurchaseJson(String invoiceNumber, String customerName, String dateTime, Double totalValue, String currencyCode, String... productIds) {
        String productIdList = "[\"" + String.join("\",\"", productIds) + "\"]";
        return String.format(Locale.US,"{\"invoiceNumber\":\"%s\",\"customerName\":\"%s\",\"dateTime\":\"%s\",\"productIds\":%s,\"amount\":%.2f,\"currencyCode\":\"%s\"}", invoiceNumber, customerName, dateTime, productIdList, totalValue, currencyCode);
    }

    @Test
    public void testPurchaseCurrencyCodeEUR() throws Exception {
        String body = getPurchaseJson("1", "customer 1", "2020-01-01T10:00:00+01:00", 25.34, "EUR", "product1");
        mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON).content(body)
        ).andExpect(status().isOk());

        Purchase savedPurchase = purchasesRepository.list().get(purchasesRepository.list().size() - 1);
        assertEquals("customer 1", savedPurchase.getCustomerName());
        assertEquals("1", savedPurchase.getInvoiceNumber());
        assertEquals("2020-01-01T10:00:00", savedPurchase.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
        assertEquals(25.34, savedPurchase.getTotalValue());
    }

    @Test
    public void testPurchaseCurrencyCodeUSD() throws Exception {
        String body = getPurchaseJson("1", "customer 1", "2020-01-01T10:00:00+01:00", 25.34, "USD", "product1");
        mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON).content(body)
        ).andExpect(status().isOk());

        Purchase savedPurchase = purchasesRepository.list().get(purchasesRepository.list().size() - 1);
        assertEquals("customer 1", savedPurchase.getCustomerName());
        assertEquals("1", savedPurchase.getInvoiceNumber());
        assertEquals("2020-01-01T10:00:00", savedPurchase.getTimestamp().format(DateTimeFormatter.ISO_DATE_TIME));
        assertEquals(25.34/1.1187, savedPurchase.getTotalValue());
    }


    @Test
    public void testPurchaseStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDate = now.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));
      // Clear existing data
        purchasesRepository.deleteAll();
        // Inside window purchases
        purchasesRepository.save(new Purchase("1", firstDate, Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("2", firstDate.plusDays(1), Collections.emptyList(), "c3", 10.0));
        purchasesRepository.save(new Purchase("3", firstDate.plusDays(2), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("4", firstDate.plusDays(3), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("5", firstDate.plusDays(4), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("6", firstDate.plusDays(5), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("7", firstDate.plusDays(6), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("8", firstDate.plusDays(7), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("9", firstDate.plusDays(8), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("10", firstDate.plusDays(9), Collections.emptyList(), "c1", 10.0));
        purchasesRepository.save(new Purchase("11", firstDate.plusDays(30), Collections.emptyList(), "c1", 21.0));
        

        // Outside window purchases
        purchasesRepository.save(new Purchase("31", now.minusDays(31), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("32", now.minusDays(31), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("33", now.minusDays(32), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("34", now.minusDays(33), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("35", now.minusDays(34), Collections.emptyList(), "c2", 10.0));
        purchasesRepository.save(new Purchase("36", now.minusDays(35), Collections.emptyList(), "c2", 10.0));

        ResponseEntity res=purchasesController.getStats("","EUR");
    	PurchaseStats purchaseStats =(PurchaseStats) res.getBody();
        
        assertEquals(formatter.format(firstDate), purchaseStats.getFrom());
        assertEquals(formatter.format(firstDate.plusDays(30)), purchaseStats.getTo());
        assertEquals(11, purchaseStats.getCountPurchases());
        assertEquals(121.0, purchaseStats.getTotalAmount());
        assertEquals(11, purchaseStats.getAvgAmount());
        assertEquals(10.0, purchaseStats.getMinAmount());
        assertEquals(21.0, purchaseStats.getMaxAmount());
       
    }
    
    @Test
    public void testPurchaseStatisticsWithoutAnyPurchase() {
       
    	ResponseEntity res=purchasesController.getStats("","EUR");
    	PurchaseStats purchaseStats =(PurchaseStats) res.getBody();
        
        assertEquals("", purchaseStats.getFrom());
        assertEquals("", purchaseStats.getTo());
        assertEquals(0, purchaseStats.getCountPurchases());
        assertEquals(0, purchaseStats.getTotalAmount());
        assertEquals(0, purchaseStats.getAvgAmount());
        assertEquals(0, purchaseStats.getMinAmount());
        assertEquals(0, purchaseStats.getMaxAmount());
       
    }
    
    @Test
    public void testPurchaseStatisticsForCustomer() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime firstDate = now.minusDays(30);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));
        // Clear existing data
        purchasesRepository.deleteAll();
        // Inside window purchases
        purchasesRepository.save(new Purchase("1", firstDate, Collections.emptyList(), "customer 1", 10.0));
       
        purchasesRepository.save(new Purchase("2", now.minusDays(33), Collections.emptyList(), "customer 1", 10.0));
 
        ResponseEntity res=purchasesController.getStats("customer 1","EUR");
    	PurchaseStats purchaseStats =(PurchaseStats) res.getBody();
        
        assertEquals(formatter.format(firstDate), purchaseStats.getFrom());
        assertEquals(1, purchaseStats.getCountPurchases());
        assertEquals(10.0, purchaseStats.getTotalAmount());
        assertEquals(10.0, purchaseStats.getAvgAmount());
        assertEquals(10.0, purchaseStats.getMinAmount());
        assertEquals(10.0, purchaseStats.getMaxAmount());
        purchasesRepository.deleteAll();
    }
    
    @Test
    public void testPurchaseInvalidRequest() throws Exception {
        String body = getPurchaseJson("1", "customer 1", "2020-01-01", 25.34, "USD", "product1");
        ResultActions result=mockMvc.perform(post("/purchases")
                .contentType(MediaType.APPLICATION_JSON).content(body));
        assertEquals(HttpServletResponse.SC_BAD_REQUEST, result.andReturn().getResponse().getStatus());
    }
    
   
}
