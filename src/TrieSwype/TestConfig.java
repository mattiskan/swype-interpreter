package TrieSwype;

import java.io.File;
import java.io.FileFilter;

public class TestConfig {
	boolean optimizeWords = true;
	boolean curveDistance = true;
	int maxDistance = 80;
	String wordList = "files/ordlista-stor.txt";
	boolean showGraphics = false;
	String wordDir = "files";
	FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith("0.json");
		}
	};
}
