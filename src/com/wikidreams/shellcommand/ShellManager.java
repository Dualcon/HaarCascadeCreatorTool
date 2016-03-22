package com.wikidreams.shellcommand;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShellManager {

	private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ShellManager.class);

	public static String executeCommand(ArrayList<String> command) {
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();
			return output(process.getInputStream());
		} catch (IOException e) {
			ShellManager.logger.error(e.getMessage());;			
		}
		return "Error: We've got some problems here.";
	}

	private static String output(InputStream inputStream) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + System.getProperty("line.separator"));
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}

}
