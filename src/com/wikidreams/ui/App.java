package com.wikidreams.ui;

import java.io.File;

import com.wikidreams.properties.PropertiesManager;

public class App {

	public static void main(String[] args) {
		// Load project properties.
		PropertiesManager.loadProperties("resources/config.properties");

		// Create workspace.
		File file = new File(PropertiesManager.properties.get("WorkSpace"));
		if (! file.exists()) {
			file.mkdir();
		}

		// Create dialog menu.
		DialogManager.createMenu();
	}

}
