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


	static {
		PropertiesManager.loadProperties("resources/config.properties");
	}

	private String processName;
	private File[] positiveImages;
	private File bgFile;
	private String samplesByImage;
	private String maxxangle;
	private String maxyangle;
	private String maxzangle;
	private String width;
	private String height;
	private String num;
	private String numPos;
	private String numNeg;
	private String numStages;

	private File processDir;
	private File infoFile;


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

		// Create workspace folder.
		File dir = new File(PropertiesManager.properties.get("OpenCVBin").trim() + "\\Workspace");
		if (! dir.exists()) {
			dir.mkdir();		
		}

		// Create cascades folder.
		dir = new File(PropertiesManager.properties.get("OpenCVBin").trim() + "\\Workspace\\Cascades");
		if (! dir.exists()) {
			dir.mkdir();
		}

		// Create samples.
		this.createSamples();
	}



	private void createSamples() {
		// Create process folder.
		this.processDir = new File(PropertiesManager.properties.get("OpenCVBin").trim() + "\\Workspace\\Cascades\\" + this.processName);
		if (this.processDir.exists()) {
			JOptionPane.showMessageDialog(null, "A process with this name already exists.");
			return;
		}
		this.processDir.mkdir();

		// Create info folder.
		File infoDir = new File(this.processDir + "\\info\\");
		infoDir.mkdir();

		// Create info.lst file.
		this.infoFile = new File(infoDir.getAbsolutePath() + "\\info.lst");

		StringBuilder sbInfoFile = new StringBuilder();
		File reportFile = new File(this.processDir + "\\samples_report.log");
		StringBuilder sbReportFile = new StringBuilder();

		for (File f : this.positiveImages) {

			// Create samples.bat file		
			StringBuilder b = new StringBuilder();
			b.append(PropertiesManager.properties.get("OpenCVBin").trim() + "\\opencv_createsamples.exe");
			b.append(" -info ");
			b.append(infoDir.getAbsolutePath() + "\\info.lst");
			b.append(" -pngoutput ");
			b.append(infoDir.getAbsolutePath());
			b.append(" -img ");
			b.append(f.getAbsolutePath());
			b.append(" -bg ");
			b.append(this.bgFile.getAbsolutePath());
			b.append(" -num ");
			b.append(this.samplesByImage);
			b.append(" -maxxangle ");
			b.append(this.maxxangle);
			b.append(" -maxyangle ");
			b.append(this.maxyangle);
			b.append(" -maxzangle ");
			b.append(this.maxzangle);
			b.append(" -w ");
			b.append(this.width);
			b.append(" -h ");
			b.append(this.height);
			try {
				File tFile = new File(this.processDir.getAbsolutePath() + "\\create_samples.bat");
				FileWriter tFileWriter = new FileWriter(tFile);
				BufferedWriter tBufferedWriter = new BufferedWriter(tFileWriter);
				tBufferedWriter.write(b.toString());
				tBufferedWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			// Execute samples.bat file.
			ArrayList<String> command = new ArrayList<>();
			command.add(this.processDir.getAbsolutePath() + "\\create_samples.bat");
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
		File vecDir = new File(this.processDir.getAbsolutePath() + "\\vector");
		vecDir.mkdir();

		// Get number of created images.
		File infoDir = new File(this.processDir.getAbsolutePath() + "\\info");
		File[] allFiles = infoDir.listFiles();
		this.num = Integer.toString(allFiles.length - 1);

		// Create vector.bat file.
		StringBuilder b = new StringBuilder();
		b.append(PropertiesManager.properties.get("OpenCVBin").trim() + "\\opencv_createsamples.exe");
		b.append(" -info ");
		b.append(this.processDir.getAbsolutePath() + "\\info\\info.lst");
		b.append(" -num ");
		b.append(this.num);
		b.append(" -w ");
		b.append(this.width);
		b.append(" -h ");
		b.append(this.height);
		b.append(" -vec ");
		b.append(this.processDir.getAbsolutePath() + "\\vector\\samples.vec");
		try {
			File tFile = new File(this.processDir.getAbsolutePath() + "\\create_vector.bat");
			FileWriter tFileWriter = new FileWriter(tFile);
			BufferedWriter tBufferedWriter = new BufferedWriter(tFileWriter);
			tBufferedWriter.write(b.toString());
			tBufferedWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Execute create_vector.bat file.
		ArrayList<String> command = new ArrayList<>();
		command.add(this.processDir.getAbsolutePath() + "\\create_vector.bat");
		String output = ShellManager.executeCommand(command);

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Create vector report file.
		try {
			File reportFile = new File(this.processDir.getAbsolutePath() + "\\vector_report.log");
			FileWriter reportFileWriter = new FileWriter(reportFile);
			BufferedWriter reportBufferedWriter = new BufferedWriter(reportFileWriter);
			reportBufferedWriter.write(output);
			reportBufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create train cascade file.
		this.createCascade();
	}



	private void createCascade() {
		// Create data folder.
		File dataDir = new File(this.processDir.getAbsolutePath() + "\\data");
		dataDir.mkdir();

		// Create bat file content.
		StringBuilder b = new StringBuilder();
		b.append(PropertiesManager.properties.get("OpenCVBin").trim() + "\\opencv_traincascade.exe");
		b.append(" -data " + dataDir.getAbsolutePath());
		b.append(" -vec " + this.processDir.getAbsolutePath() + "\\vector\\samples.vec");
		b.append(" -bg " + this.bgFile.getAbsolutePath());
		b.append(" -numPos " + this.numPos);
		b.append(" -numNeg " + this.numNeg);
		b.append(" -numStages " + this.numStages);
		b.append(" -w " + this.width);
		b.append(" -h " + this.height);
		b.append(" > " + this.processDir.getAbsolutePath() + "\\cascade_report.log");
		File tFile = new File(PropertiesManager.properties.get("OpenCVBin").trim() + "\\" + this.processName + "_train.bat");
		try {
			FileWriter tFileWriter = new FileWriter(tFile);
			BufferedWriter tBufferedWriter = new BufferedWriter(tFileWriter);
			tBufferedWriter.write(b.toString());
			tBufferedWriter.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// Alert user to start the process.
		JOptionPane.showMessageDialog(null, "Start the process with the file: " + tFile.getAbsolutePath());
	}



}
