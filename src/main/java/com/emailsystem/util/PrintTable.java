package main.java.com.emailsystem.util;

import java.util.List;

public class PrintTable {
	public void printBox(List<String[]> headers, List<String[]> rows) {
		if ((headers == null || headers.isEmpty()) && (rows == null || rows.isEmpty())) {
            return;
        }
		int numOfColumns = rows.get(0).length;
		int[] eachColumnWidth = new int[numOfColumns];
		for(String[] header: headers) {
			for(int i = 0; i<numOfColumns; i++) {
				eachColumnWidth[i] = Math.max(eachColumnWidth[i], header[i].length());
			}
		}
		for(String[] row: rows) {
			for(int i = 0; i<numOfColumns; i++) {
				eachColumnWidth[i] = Math.max(eachColumnWidth[i], row[i].length());
			}
		}
		
		String border = getBorder(eachColumnWidth);
		
		
		System.out.println(border);
		for(String[] header: headers) {
			String head = printRow(header, eachColumnWidth);
			System.out.println(head);
			System.out.println(border);
		}
		for(String[] row: rows) {
			String r = printRow(row, eachColumnWidth);
			System.out.println(r);
		}
		System.out.println(border);
	}
	
	public String getBorder(int[] eachColumnWidth) {
		StringBuilder sb = new StringBuilder();
		sb.append("+");
		for(int colWidth : eachColumnWidth) {
			sb.append("-".repeat(colWidth+2));
			sb.append("+");
		}
		return sb.toString();
	}
	
	public String printRow(String[] cells, int[] eachColumnWidth) {
		StringBuilder sb = new StringBuilder();
		sb.append("|");
		for(int i =0 ; i<cells.length; i++) {
			sb.append(" ");
			sb.append(String.format("%-"+eachColumnWidth[i]+"s", cells[i]));
			sb.append(" |");
		}
		return sb.toString();
	}
	
}
