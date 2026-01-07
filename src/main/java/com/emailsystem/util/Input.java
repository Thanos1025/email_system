package main.java.com.emailsystem.util;

import java.util.Scanner;

public class Input{
	Scanner sc;
	public Input() {
		sc = new Scanner(System.in);
	}
	
	public int nextInt() {
	    while (true) {
	        try {
	            int userInput = sc.nextInt();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("Please enter a valid input:");
	            sc.nextLine();
	        }
	    }
	}

	
	public double nextDouble() {
		while (true) {
	        try {
	            double userInput = sc.nextDouble();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("Please enter a valid input:");
	            sc.nextLine();
	        }
	    }
	}
	
	public String nextLine() {
		while (true) {
	        try {
	            String userInput = sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("Please enter a valid input:");
	            sc.nextLine();
	        }
	    }
	}
	
	public byte nextByte() {
		while (true) {
	        try {
	            Byte userInput = sc.nextByte();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("Please enter a valid input:");
	            sc.nextLine();
	        }
	    }
	}
	
	
}
