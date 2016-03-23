package com.wikidreams.haarcascade;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import com.wikidreams.properties.PropertiesManager;
import com.wikidreams.shellcommand.ShellManager;

public class HaarCascadeManager {

	private File processDir;
	private String processName;

	private File[] positiveImages;
	private File bgFile;
	private String samplesByImage;
	private String maxxangle;
	private String maxyangle;
	private String maxzangle;
	private String width;
	private String height;
	private File infoDir;
	private File infoFile;

	private File vecDir;
	private String num;

	private File dataDir;
	private String numPos;
	private String numNeg;
	private String numStages;

	public HaarCascadeManager(String processName, File[] positiveImages, File bgFile,
			String samplesByImage, String maxxangle, String maxyangle, String maxzangle,
			String width, String height, String numPos, String numNeg, String numStages) {
		super();
		this.processName = processName;
		this.positiveImages = positiveImages;
		this.bgFile = bgFile;
		this.samplesByImage = samplesByImage;
		this.maxxangle = maxxangle;
		this.maxyangle = maxyangle;
		this.maxzangle = maxzangle;
		this.width = width;
		this.height = height;
		this.numPos = numPos;
		this.numNeg = numNeg;
		this.numStages = numStages;

		// Create samples.
		this.createSamples();
	}

	public void createSamples() {

		// Create process folder.
		this.processDir = new File(PropertiesManager.properties.get("WorkSpace") + this.processName);
		if (this.processDir.exists()) {
			JOptionPane.showMessageDialog(null, "A process with this name already exists.");
			return;
		}
		this.processDir.mkdir();

		// Create info folder.
		this.infoDir = new File(this.processDir + "\\info\\");
		this.infoDir.mkdir();

		this.infoFile = new File(this.infoDir.getAbsolutePath() + "\\info.lst");
		StringBuilder sbInfoFile = new StringBuilder();

		File reportFile = new File(this.processDir + "\\samples_report.log");
		StringBuilder sbReportFile = new StringBuilder();

		for (File f : this.positiveImages) {

			ArrayList<String> command = new ArrayList<>();	
			command.add(PropertiesManager.properties.get("OpenCVBin") + "\\opencv_createsamples.exe");
			command.add("-info");
			command.add(this.infoDir.getAbsolutePath() + "\\info.lst");
			command.add("-pngoutput");
			command.add(this.infoDir.getAbsolutePath());
			command.add("-img");
			command.add(f.getAbsolutePath());
			command.add("-bg");
			command.add(this.bgFile.getAbsolutePath());
			command.add("-num");
			command.add(this.samplesByImage);
			command.add("-maxxangle");
			command.add(this.maxxangle);
			command.add("-maxyangle");
			command.add(this.maxyangle);
			command.add("-maxzangle");
			command.add(this.maxzangle);
			command.add("-w");
			command.add(this.width);
			command.add("-h");
			command.add(this.height);
			String output = ShellManager.executeCommand(command);

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			sbReportFile.append(output);
			sbReportFile.append("\n");

			if (this.infoFile.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(this.infoFile.getAbsolutePath()));
					String line = br.readLine();
					while (line != null) {
						sbInfoFile.append(line);
						sbInfoFile.append("\n");
						line = br.readLine();
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}

		// Save info and create samples report files.
		try{
			FileWriter infoFileWriter = new FileWriter(infoFile);
			BufferedWriter infoBufferedWriter = new BufferedWriter(infoFileWriter);
			infoBufferedWriter.write(sbInfoFile.toString());
			infoBufferedWriter.close();

			FileWriter reportFileWriter = new FileWriter(reportFile);
			BufferedWriter reportBufferedWriter = new BufferedWriter(reportFileWriter);
			reportBufferedWriter.write(sbReportFile.toString());
			reportBufferedWriter.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		// Create vector file.
		this.createVectorFile();
	}



	private void createVectorFile() {
		// Create vector folder.
		this.vecDir = new File(PropertiesManager.properties.get("WorkSpace") + this.processName + "\\vector");
		this.vecDir.mkdir();

		// Get number of created images.
		ArrayList<File> allFiles = new ArrayList<>();
		allFiles = this.loadFiles(this.infoDir.getAbsolutePath());
		this.num = Integer.toString(allFiles.size() - 1);

		ArrayList<String> command = new ArrayList<>();	
		command.add(PropertiesManager.properties.get("OpenCVBin") + "\\opencv_createsamples.exe");
		command.add("-info");
		command.add(this.infoDir.getAbsolutePath() + "\\info.lst");
		command.add("-num");
		command.add(this.num);
		command.add("-w");
		command.add(this.width);
		command.add("-h");
		command.add(this.height);
		command.add("-vec");
		command.add(this.vecDir + "\\samples.vec");
		String output = ShellManager.executeCommand(command);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Create vector report file.
		try {
			File reportFile = new File(PropertiesManager.properties.get("WorkSpace") + this.processName + "\\vector_report.log");
			FileWriter reportFileWriter = new FileWriter(reportFile);
			BufferedWriter reportBufferedWriter = new BufferedWriter(reportFileWriter);
			reportBufferedWriter.write(output);
			reportBufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.createCascade();
	}



	private void createCascade() {
		// Create data folder.
		this.dataDir = new File(PropertiesManager.properties.get("WorkSpace") + this.processName + "\\data");
		this.dataDir.mkdir();

		// Create bat file content.
		StringBuilder c = new StringBuilder();
		c.append(PropertiesManager.properties.get("OpenCVBin") + "opencv_traincascade.exe");
		c.append(" -data " + this.dataDir.getAbsolutePath());
		c.append(" -vec " + this.vecDir.getAbsolutePath() + "\\samples.vec");
		c.append(" -bg " + this.bgFile.getAbsolutePath());
		c.append(" -numPos " + this.numPos);
		c.append(" -numNeg " + this.numNeg);
		c.append(" -numStages " + this.numStages);
		c.append(" -w " + this.width);
		c.append(" -h " + this.height);
		c.append(" > " + PropertiesManager.properties.get("WorkSpace") + this.processName + "\\cascade_report.log");

		// Create cascade bat file.
		try {
			File reportFile = new File(PropertiesManager.properties.get("OpenCVBin") + this.processName + "_cascade.bat");
			FileWriter reportFileWriter = new FileWriter(reportFile);
			BufferedWriter reportBufferedWriter = new BufferedWriter(reportFileWriter);
			reportBufferedWriter.write(c.toString());
			reportBufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private void displayDirectoryContents(File dir, ArrayList<File> result) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getAbsolutePath());
					displayDirectoryContents(file, result);
				} else {
					//System.out.println("     file:" + file.getAbsolutePath());
					result.add(new File(file.getAbsolutePath()));
				}		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<File> loadFiles(String path) {
		ArrayList<File> files = new ArrayList<>();
		this.displayDirectoryContents(new File(path), files);
		return files;
	}

}
