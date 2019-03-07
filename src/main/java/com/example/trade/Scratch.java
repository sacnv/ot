package com.example.trade;

import java.util.Arrays;
import java.util.List;

public class Scratch {

	public static void main(String[] args) {
		List<String> stooges = Arrays.asList("Larry", "Moe", "Curly");
		stooges.add("charlie");
		
		stooges.stream().forEach(System.out::println);
	}

}
