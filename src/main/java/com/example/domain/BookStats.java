package com.example.domain;

import java.math.BigDecimal;
import java.util.LongSummaryStatistics;
import java.util.Map;

public class BookStats {
	
	private LongSummaryStatistics orderStats;
	
	private Order earliest;
	
	private Order latest;
	
	private Map<Boolean, LongSummaryStatistics> orderStatsByValidity;
	
	private Map<BigDecimal,Long> limitTable;
	
	private LongSummaryStatistics execStats;
	
	private BigDecimal execPrice = BigDecimal.ZERO;

	public LongSummaryStatistics getOrderStats() {
		return orderStats;
	}

	public void setOrderStats(LongSummaryStatistics orderStats) {
		this.orderStats = orderStats;
	}

	public Order getEarliest() {
		return earliest;
	}

	public void setEarliest(Order earliest) {
		this.earliest = earliest;
	}

	public Order getLatest() {
		return latest;
	}

	public void setLatest(Order latest) {
		this.latest = latest;
	}

	public Map<Boolean, LongSummaryStatistics> getOrderStatsByValidity() {
		return orderStatsByValidity;
	}

	public void setOrderStatsByValidity(Map<Boolean, LongSummaryStatistics> orderStatsByValidity) {
		this.orderStatsByValidity = orderStatsByValidity;
	}

	public Map<BigDecimal, Long> getLimitTable() {
		return limitTable;
	}

	public void setLimitTable(Map<BigDecimal, Long> limitTable) {
		this.limitTable = limitTable;
	}

	public LongSummaryStatistics getExecStats() {
		return execStats;
	}

	public void setExecStats(LongSummaryStatistics execStats) {
		this.execStats = execStats;
	}

	public BigDecimal getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(BigDecimal execPrice) {
		this.execPrice = execPrice;
	}
	
	

}
