package com.journi.challenge.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseRequest;
import com.journi.challenge.models.PurchaseStats;
import com.journi.challenge.repositories.PurchasesRepository;

@RestController
public class PurchasesController {

	@Inject
	private PurchasesRepository purchasesRepository;
	@Inject
	private CurrencyConverter currencyConverter;

	/**
	 * This method will return statistics for the customer for last 30 days if
	 * customer name is passed. else it will return statistics for all the customer
	 * for the duration.
	 * 
	 * @param customerName customerName
	 * @return purchase Stats
	 */
	@GetMapping("/purchases/statistics")
	public ResponseEntity getStats(@RequestParam(name = "customerName", defaultValue = "") String customerName,
			@RequestParam(name = "currencyCode", defaultValue = "EUR") String currencyCode) {
		 return new ResponseEntity<PurchaseStats>(purchasesRepository.getLast30DaysStats(customerName,currencyCode), HttpStatus.OK);
	}

	@PostMapping("/purchases")
	public ResponseEntity save(@RequestBody PurchaseRequest purchaseRequest) {

		try {
			// convert currency to equalent Euro value
			Double currencyValue = currencyConverter.convertCurrencyToEuro(purchaseRequest.getCurrencyCode(),
					purchaseRequest.getAmount());

			Purchase newPurchase = new Purchase(purchaseRequest.getInvoiceNumber(),
					LocalDateTime.parse(purchaseRequest.getDateTime(), DateTimeFormatter.ISO_DATE_TIME),
					purchaseRequest.getProductIds(), purchaseRequest.getCustomerName(), currencyValue
					);
			purchasesRepository.save(newPurchase);
			return new ResponseEntity<Purchase>(newPurchase, HttpStatus.OK);
			
		} catch (Exception e) {

		      Logger.getLogger(PurchasesController.class.getName()).log(Level.SEVERE, null, e);
		      return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		    
		}
	}
	
	
}
