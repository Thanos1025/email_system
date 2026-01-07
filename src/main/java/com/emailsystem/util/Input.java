package main.java.com.emailsystem.util;

import java.util.Scanner;

public class Input{
	Scanner sc;
	public Input() {
		sc = new Scanner(System.in);
	}
	
	public int nextInt(String str) {
	    while (true) {
			System.out.print(str);
	        try {
	            int userInput = sc.nextInt();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("\nPlease enter a valid input\n");
	            sc.nextLine();
	        }
	    }
	}

	
	public double nextDouble(String str) {
		while (true) {
			System.out.print(str);
	        try {
	            double userInput = sc.nextDouble();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("\nPlease enter a valid input\n");
	            sc.nextLine();
	        }
	    }
	}
	
	public String nextLine(String str) {
		while (true) {
			System.out.print(str);
	        try {
	            String userInput = sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("\nPlease enter a valid input\n");
	            sc.nextLine();
	        }
	    }
	}
	
	public byte nextByte(String str) {
		while (true) {
			System.out.print(str);
	        try {
	            Byte userInput = sc.nextByte();
	            sc.nextLine();
	            return userInput;
	        } catch (Exception e) {
	            System.out.println("\nPlease enter a valid input\nk");
	            sc.nextLine();
	        }
	    }
	}
	
	
}
