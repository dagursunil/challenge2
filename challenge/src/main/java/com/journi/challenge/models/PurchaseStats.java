package com.journi.challenge.models;

public class PurchaseStats {

    private final String from;
    private final String to;
    private final Long countPurchases;
    private final Double totalAmount;
    private final Double avgAmount;
    private final Double minAmount;
    private final Double maxAmount;

    public PurchaseStats(String from, String to, Long countPurchases, Double totalAmount, Double avgAmount, Double minAmount, Double maxAmount) {
        this.from = from;
        this.to = to;
        this.countPurchases = countPurchases;
        this.totalAmount = totalAmount;
        this.avgAmount = avgAmount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Long getCountPurchases() {
        return countPurchases;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public Double getAvgAmount() {
        return avgAmount;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

}
