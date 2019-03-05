package com.example.domain;

import java.util.LongSummaryStatistics;
import java.util.Map;

public class BookStats {
	
	private LongSummaryStatistics orderStats;
	
	private Order earliest;
	
	private Order latest;
	
	private Map<Boolean, LongSummaryStatistics> orderStatsByValidity;
	
	private Map<Long,Long> limitTable;
	
	private LongSummaryStatistics execStats;
	
	private Long execPrice = 0L;

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

	public Map<Long, Long> getLimitTable() {
		return limitTable;
	}

	public void setLimitTable(Map<Long, Long> limitTable) {
		this.limitTable = limitTable;
	}

	public LongSummaryStatistics getExecStats() {
		return execStats;
	}

	public void setExecStats(LongSummaryStatistics execStats) {
		this.execStats = execStats;
	}

	public Long getExecPrice() {
		return execPrice;
	}

	public void setExecPrice(Long execPrice) {
		this.execPrice = execPrice;
	}
	
	

}
