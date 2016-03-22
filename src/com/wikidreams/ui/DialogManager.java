package com.wikidreams.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.wikidreams.haarcascade.HaarCascadeManager;
import com.wikidreams.properties.PropertiesManager;

public class DialogManager {

	private static File[] positiveImages;
	private static File bgFile;

	private static File userDir;


	public static void createMenu() {
		DialogManager.userDir = new File(PropertiesManager.properties.get("WorkSpace"));

		JTextField processName = new JTextField();
		processName.setToolTipText("Process Name");

		JButton img = new JButton("-img");
		img.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DialogManager.userDir);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					DialogManager.positiveImages = fc.getSelectedFiles();
					DialogManager.userDir = fc.getCurrentDirectory();
				}		
			}
		});

		JButton bg = new JButton("-bg");
		bg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(DialogManager.userDir);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					DialogManager.bgFile = fc.getSelectedFile();
				}		
			}
		});

		JTextField num = new JTextField();
		num.setToolTipText("-num");

		JTextField maxxangle = new JTextField();
		maxxangle.setToolTipText("-maxxangle");

		JTextField maxyangle = new JTextField();
		maxyangle.setToolTipText("-maxyangle");

		JTextField maxzangle = new JTextField();
		maxzangle.setToolTipText("-maxzangle");

		JTextField width = new JTextField();
		width.setToolTipText("-w");

		JTextField height = new JTextField();
		height.setToolTipText("-h");

		JTextField numPos = new JTextField();
		numPos.setToolTipText("-numPos");

		JTextField numNeg = new JTextField();
		numNeg.setToolTipText("-numNeg");

		JTextField numStages = new JTextField();
		numStages.setToolTipText("-numStages");

		Object[] message = {
				"processName:", processName,
				"img:", img,
				"bg:", bg,
				"num:", num,
				"maxxangle:", maxxangle,
				"maxyangle:", maxyangle,
				"maxzangle:", maxzangle,
				"width:", width,
				"height:", height,
				"numPos:", numPos,
				"numNeg:", numNeg,
				"numStages:", numStages
		};

		int option = JOptionPane.showConfirmDialog(null, message, "Create samples.", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			new HaarCascadeManager(processName.getText(), DialogManager.positiveImages, DialogManager.bgFile, num.getText(), maxxangle.getText(), maxyangle.getText(), maxzangle.getText(), width.getText(), height.getText(), numPos.getText(), numNeg.getText(), numStages.getText());
		}
	}

}
