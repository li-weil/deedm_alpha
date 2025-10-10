/**
 * 
 */
package com.deedm.legacy.util;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * @author user
 *
 */
public class Configuration {

	public static String CONFIG_FILE = "Deedm.ini";
	public static String DATA_PATH_KEY = "DataPath";
	public static String GRAPHVIZ_PATH_KEY = "GraphVizPath";
	public static String CONFIG_SPLITOR = " = ";
	
	public static String imageFilePath = ".\\img\\";
	public static String dataFilePath = ".\\data\\";
	public static String graphVizPath = "D:\\GraphViz\\Graphviz\\bin\\";
	public static int laTeXImageFontSize = 20;
	public static Color laTeXImageBackground = Color.WHITE;
	public static Color laTeXImageForeground = Color.BLACK;
	public static Font plainStringFont = new Font("¿¬Ìå", Font.BOLD, 24);
	public static Color plainStringBackground = null;
	public static Color plainStringForeground = null;
	public static String treeVizLayout = "dot";
	public static String graphVizLayout = "circo";
	
	

	private Configuration() {
	}
	
	public static void load() {
		String currentPath = System.getProperty("user.dir");
		System.out.println("Current path : " + currentPath);
		
		File configFile = new File(CONFIG_FILE);
		if (configFile.exists()) {
			try {
				FileInputStream fileIn = new FileInputStream(configFile);
				
				final Scanner in = new Scanner(fileIn);
				while (in.hasNextLine()) {
					String line = in.nextLine().trim();
					String[] configValues = line.split(CONFIG_SPLITOR);
					String configKey = configValues[0].trim();
					if (configKey.contentEquals(DATA_PATH_KEY)) {
						if (configValues.length >= 2) dataFilePath = configValues[1].trim();
					} else if (configKey.contentEquals(GRAPHVIZ_PATH_KEY)) {
						if (configValues.length >= 2) graphVizPath = configValues[1].trim();
					}
				}
				in.close();
			} catch (IOException exc) {
				System.out.println("Can not read file " + configFile.getAbsolutePath());
			}
		}
	}
	
	public static void save() {
		try {
			PrintWriter writer = new PrintWriter(CONFIG_FILE);
			writer.println(DATA_PATH_KEY + CONFIG_SPLITOR + dataFilePath);
			writer.println(GRAPHVIZ_PATH_KEY + CONFIG_SPLITOR + graphVizPath);
			writer.close();
		} catch (IOException exc) {
			System.out.println("Can not write content to file " + CONFIG_FILE);
		}
	}
}
