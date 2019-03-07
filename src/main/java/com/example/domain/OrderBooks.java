package com.example.domain;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * Model class to hold collection of Order Books
 * 
 */
public class OrderBooks {
	
	private static List<OrderBook> books = new CopyOnWriteArrayList<OrderBook>();

	public static List<OrderBook> getBooks() {
		return books;
	}

	public static void setBooks(List<OrderBook> books) {
		OrderBooks.books = books;
	}
	
	
	
}
