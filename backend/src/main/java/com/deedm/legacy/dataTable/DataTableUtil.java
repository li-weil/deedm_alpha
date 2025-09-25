package com.deedm.legacy.dataTable;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * A class to provide some static methods to utilize data table to generate some useful data or table
 * 
 * @author Zhou Xiaocong
 * @since 2020��09��25��
 * @version 1.0
 */
public class DataTableUtil {

	/**
	 * �����ݱ� table ������д�ɿ����� LaTex ��Ϊ������ʾ����ʽ������ columnNames ����ѡ�� table �е��С�mergeLine ��ʾ table �еļ������ݺϲ�Ϊ LaTex �б����һ�н��д�ӡ����
	 * ѡ��� table ���бȽ��ٵ�ʱ����Լ������ݴ�ӡ�� LaTex�����е�һ�С� 
	 */
	public static void writeDataLinesAsLatexTableLines(PrintWriter writer, DataTableManager table, String[] columnNames, int mergeLine) throws IOException {
		if (columnNames == null) columnNames = table.getColumnNameArray();
		if (mergeLine <= 0) mergeLine = 1;
		
		writer.print("\\begin{tabular}{");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) writer.print("c");
				else writer.print("|c");
			}
		}
		writer.println("}");
		
		writer.print("\\hline");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) writer.print("{\\heiti " + columnNames[colIndex] + "} ");
				else writer.print(" & " + "{\\heiti " + columnNames[colIndex] + "} ");
			}
		}
		writer.println(" \\\\");
		
		int index = 0;
		while (index < table.getLineNumber()) {
			writer.print("\\hline ");
			for (int i = 0; i < mergeLine; i++) {
				String[] lineStringArray = null;
				if (index < table.getLineNumber()) {
					lineStringArray = table.getLineAsStringArray(index, columnNames);
				}
				for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
					String columnString = " ";
					if (lineStringArray != null) columnString = lineStringArray[colIndex];
					
/*					if (colIndex == 0) {
						if (i == 0) writer.print("\\pname{" + columnString + "} ");
						else writer.print(" & \\pname{" + columnString + "} ");
					} else writer.print(" & " + columnString);*/
					if (colIndex == 0) {
					if (i == 0) writer.print(columnString);
					else writer.print(" & " + columnString);
				} else writer.print(" & " + columnString);
				}
				index = index + 1;
			}
			writer.println(" \\\\");
		}
		
		writer.println("\\hline");
		writer.println("\\end{tabular}");
	}
	
	public static void writeDataLinesAsLatexTableLines(PrintWriter writer, DataTableManager table, String[] columnNames) throws IOException {
		writeDataLinesAsLatexTableLines(writer, table, columnNames, 1);
	}

	/**
	 * �����ݱ� table ������д�ɿ����� LaTex ��Ϊ������ʾ����ʽ������ columnNames ����ѡ�� table �е��С�mergeLine ��ʾ table �еļ������ݺϲ�Ϊ LaTex �б����һ�н��д�ӡ����
	 * ѡ��� table ���бȽ��ٵ�ʱ����Լ������ݴ�ӡ�� LaTex�����е�һ�С� 
	 */
	public static String getJMathLaTexString(DataTableManager table, String[] columnNames, int mergeLine) {
		if (columnNames == null) columnNames = table.getColumnNameArray();
		if (mergeLine <= 0) mergeLine = 1;
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("\\begin{tabular}{");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) buffer.append("c");
				else buffer.append("|c");
			}
		}
		buffer.append("}\n");
		
		buffer.append("\\hline");
		for (int i = 0; i < mergeLine; i++) {
			for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
				if (colIndex == 0 && i == 0) buffer.append("{" + columnNames[colIndex] + "} ");
				else buffer.append(" & " + "{" + columnNames[colIndex] + "} ");
			}
		}
		buffer.append(" \\\\\n");
		
		int index = 0;
		while (index < table.getLineNumber()) {
			buffer.append("\\hline ");
			for (int i = 0; i < mergeLine; i++) {
				String[] lineStringArray = null;
				if (index < table.getLineNumber()) {
					lineStringArray = table.getLineAsStringArray(index, columnNames);
				}
				for (int colIndex = 0; colIndex < columnNames.length; colIndex++) {
					String columnString = " ";
					if (lineStringArray != null) columnString = lineStringArray[colIndex];
					
/*					if (colIndex == 0) {
						if (i == 0) writer.print("\\pname{" + columnString + "} ");
						else writer.print(" & \\pname{" + columnString + "} ");
					} else writer.print(" & " + columnString);*/
					if (colIndex == 0) {
					if (i == 0) buffer.append(columnString);
					else buffer.append(" & " + columnString);
				} else buffer.append(" & " + columnString);
				}
				index = index + 1;
			}
			buffer.append(" \\\\\n");
		}
		
		buffer.append("\\hline\n");
		buffer.append("\\end{tabular}\n");
		buffer.trimToSize();
		return buffer.toString();
	}
	
	public static String getJMathLaTeXString(DataTableManager table, String[] columnNames) {
		return getJMathLaTexString(table, columnNames, 1);
	}

}

