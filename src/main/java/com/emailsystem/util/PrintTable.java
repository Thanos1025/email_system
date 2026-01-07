package main.java.com.emailsystem.util;

import java.util.LinkedList;
import java.util.List;

public class PrintTable {
	public void printBox(List<String> headersStr, List<String> rowsStr) {
		List<String[]> headers = converttoStringArrayBySeparator(headersStr);
		List<String[]> rows = converttoStringArrayBySeparator(rowsStr);
		printTable(headers, rows);
	}
	
	public void printBox(List<String> headersStr, List<String> rowsStr, Boolean ByLine) {
		if(!ByLine) {
			printBox(headersStr, rowsStr);
		}else {
			List<String> headers = breakStringToMultipleRows(headersStr);
			List<String> rows = breakStringToMultipleRows(rowsStr);
			printBox(headers, rows);
		}
	}
	public void printTable(List<String[]> headers, List<String[]> rows) {
		
		if ((headers == null || headers.isEmpty()) && (rows == null || rows.isEmpty())) {
            return;
        }
		
		
		int numOfColumns = findingNoOfColumns(headers, rows);
		int[] eachColumnWidth = new int[numOfColumns];
		for(String[] header: headers) {
			for(int i = 0; i<numOfColumns; i++) {
				if(i<header.length) {
					String clean = header[i].replace("\n", "").replace("\r", "");
					eachColumnWidth[i] = Math.max(eachColumnWidth[i], clean.length());
				}
			}
		}
		for(String[] row: rows) {
			for(int i = 0; i<numOfColumns; i++) {
				if(i<row.length) {
					String clean = row[i].replace("\n", "").replace("\r", "");
					eachColumnWidth[i] = Math.max(eachColumnWidth[i], clean.length());
				}
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
		for(int i =0 ; i<eachColumnWidth.length; i++) {
			if(i<cells.length) {
				sb.append(" ");
				String cell = cells[i].replace("\n", "").replace("\r", "");
				sb.append(String.format("%-" + eachColumnWidth[i] + "s", cell));
				sb.append(" |");
			}else {
				sb.append(" ");
				sb.append(" ".repeat(eachColumnWidth[i]));
				sb.append(" |");
			}
		}
		return sb.toString();
	}
	
	public int findingNoOfColumns(List<String[]> headers, List<String[]> rows) {
		int noOfColumns = 0;
		for(String[] header: headers) {
			noOfColumns = Math.max(header.length, noOfColumns);
		}
		for(String[] row: rows) {
			noOfColumns = Math.max(row.length, noOfColumns);
		}
		return noOfColumns;
	}
	
	public List<String[]> converttoStringArrayBySeparator(List<String> linesStr){
		List<String[]> lines = new LinkedList<>();
		for(String line: linesStr) {
			String[] lineArray = line.split("\\|!\\(\\)");
			lines.add(lineArray);
		}
		return lines;
	}
	
	public List<String> breakStringToMultipleRows(List<String> linesStr){
		List<String> newLinesStr = new LinkedList<>();
		for(String lines: linesStr) {
			String[] linesArray = lines.split("\\n");
			for(String line: linesArray) {
				newLinesStr.add(line);
			}
		}
		return newLinesStr;
	}
}
