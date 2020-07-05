package com.journi.challenge.models;

import java.util.List;

/**
 * Request for Purchase
 * amount is the value of the total purchase, in given currencyCode
 */

public class PurchaseRequest {

    private  String invoiceNumber;
    private  String customerName;
    private  String dateTime;
    private  List<String> productIds;
    private  Double amount;
    private  String currencyCode;
    
   public PurchaseRequest() {
	   
   }
    public PurchaseRequest(String invoiceNumber, String customerName, String dateTime, List<String> productIds, Double amount, String currencyCode) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.dateTime = dateTime;
        this.productIds = productIds;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }
}
