package com.journi.challenge.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.util.StringUtils;

import com.journi.challenge.CurrencyConverter;
import com.journi.challenge.models.Purchase;
import com.journi.challenge.models.PurchaseStats;

@Named
@Singleton
public class PurchasesRepository {

    private final List<Purchase> allPurchases = new ArrayList<>();
    
    @Inject
    private CurrencyConverter currencyConverter;

    /**
     * This method is used to return all purchase.
     * @return list of purchase.
     */
    public List<Purchase> list() {
        return allPurchases;
    }
/**
 * This method is used to save purchase value.
 * @param purchase
 */
    public void save(Purchase purchase) {
        allPurchases.add(purchase);
    }

    /**
     * This method will return statistics for last 30 days
     * @param customerName
     * @param currencyCode
     * @return purchase statistics.
     */
    public PurchaseStats getLast30DaysStats(String customerName,String currencyCode) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE.withZone(ZoneId.of("UTC"));

        LocalDateTime start = LocalDate.now().atStartOfDay().minusDays(30);
        List<Purchase> recentPurchases;

        if(StringUtils.isEmpty(customerName)) {
        recentPurchases = allPurchases
                .parallelStream()
                .filter(p -> p.getTimestamp().isAfter(start))
                .sorted(Comparator.comparing(Purchase::getTimestamp))
                .collect(Collectors.toList());
        
       
        }else {
        	 recentPurchases = allPurchases
                     .parallelStream()
                     .filter(p->p.getCustomerName().equalsIgnoreCase(customerName))
                     .filter(p -> p.getTimestamp().isAfter(start))
                     .sorted(Comparator.comparing(Purchase::getTimestamp))
                     .collect(Collectors.toList());
        	
        	 
        }
        recentPurchases= convertToInputCurrency(recentPurchases,currencyCode);
        

        long countPurchases = recentPurchases.size();
        double totalAmountPurchases = recentPurchases.stream().mapToDouble(Purchase::getTotalValue).sum();
       
        if(countPurchases>0) {
        return new PurchaseStats(
                formatter.format(recentPurchases.get(0).getTimestamp()),
                formatter.format(recentPurchases.get(recentPurchases.size() - 1).getTimestamp()),
                countPurchases,
                totalAmountPurchases,
                totalAmountPurchases / countPurchases,
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).min().orElse(0.0),
                recentPurchases.stream().mapToDouble(Purchase::getTotalValue).min().orElse(0.0)
        );
        }
        else {
        	 return new PurchaseStats(
                     "",
                     "",
                     countPurchases,
                     totalAmountPurchases,
                     0.0,
                     recentPurchases.stream().mapToDouble(Purchase::getTotalValue).min().orElse(0.0),
                     recentPurchases.stream().mapToDouble(Purchase::getTotalValue).min().orElse(0.0)
             );
        }
    }
    
	private List<Purchase> convertToInputCurrency(List<Purchase> recentPurchases,String currencyCode) {
		return recentPurchases.parallelStream()
				.map(a -> new Purchase(a.getInvoiceNumber(), a.getTimestamp(), a.getProductIds(), a.getCustomerName(),
						currencyConverter.convertEurToCurrency(currencyCode, a.getTotalValue())))
				.collect(Collectors.toList());

	}

/*
    * This method will remove all existing data for purchase. 
    */
   public void deleteAll() {
	   allPurchases.clear();
   }
}
