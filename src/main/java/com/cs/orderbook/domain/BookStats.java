
package com.cs.orderbook.domain;

import java.math.BigDecimal;
import java.util.Map;

public class BookStats {

    private Long totalOrders;

    private Long totalValidOrders;

    private Long totalInvalidOrders;

    private Long validDemand;

    private Long invalidDemand;

    private Long totalDemand;

    private Long accumulatedExecQuantity;

    private Order earliestOrder;

    private Order lastOrder;

    private Order biggestValidOrder;

    private Order smallestValidOrder;

    private Map<BigDecimal, Long> limitTable;

    private Map<BigDecimal, Long> validLimitTable;

    private Map<BigDecimal, Long> invalidLimitTable;

    private BigDecimal execPrice;

    public Order getEarliestOrder() {
        return earliestOrder;
    }

    public void setEarliestOrder(Order earliestOrder) {
        this.earliestOrder = earliestOrder;
    }

    public Order getLastOrder() {
        return lastOrder;
    }

    public void setLastOrder(Order lastOrder) {
        this.lastOrder = lastOrder;
    }

    public Map<BigDecimal, Long> getLimitTable() {
        return limitTable;
    }

    public void setLimitTable(Map<BigDecimal, Long> limitTable) {
        this.limitTable = limitTable;
    }

    public BigDecimal getExecPrice() {
        return execPrice;
    }

    public void setExecPrice(BigDecimal execPrice) {
        this.execPrice = execPrice;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Long getTotalValidOrders() {
        return totalValidOrders;
    }

    public void setTotalValidOrders(Long totalValidOrders) {
        this.totalValidOrders = totalValidOrders;
    }

    public Long getTotalInvalidOrders() {
        return totalInvalidOrders;
    }

    public void setTotalInvalidOrders(Long totalInvalidOrders) {
        this.totalInvalidOrders = totalInvalidOrders;
    }

    public Long getValidDemand() {
        return validDemand;
    }

    public void setValidDemand(Long validDemand) {
        this.validDemand = validDemand;
    }

    public Long getInvalidDemand() {
        return invalidDemand;
    }

    public void setInvalidDemand(Long invalidDemand) {
        this.invalidDemand = invalidDemand;
    }

    public Long getAccumulatedExecQuantity() {
        return accumulatedExecQuantity;
    }

    public void setAccumulatedExecQuantity(Long accumulatedExecQuantity) {
        this.accumulatedExecQuantity = accumulatedExecQuantity;
    }

    public Order getBiggestValidOrder() {
        return biggestValidOrder;
    }

    public void setBiggestValidOrder(Order biggestValidOrder) {
        this.biggestValidOrder = biggestValidOrder;
    }

    public Order getSmallestValidOrder() {
        return smallestValidOrder;
    }

    public void setSmallestValidOrder(Order smallestValidOrder) {
        this.smallestValidOrder = smallestValidOrder;
    }

    public Long getTotalDemand() {
        return totalDemand;
    }

    public void setTotalDemand(Long totalDemand) {
        this.totalDemand = totalDemand;
    }

    public Map<BigDecimal, Long> getValidLimitTable() {
        return validLimitTable;
    }

    public void setValidLimitTable(Map<BigDecimal, Long> validLimitTable) {
        this.validLimitTable = validLimitTable;
    }

    public Map<BigDecimal, Long> getInvalidLimitTable() {
        return invalidLimitTable;
    }

    public void setInvalidLimitTable(Map<BigDecimal, Long> invalidLimitTable) {
        this.invalidLimitTable = invalidLimitTable;
    }

}
