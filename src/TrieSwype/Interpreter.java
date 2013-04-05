package TrieSwype;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

import PermutationSwype.Curve;
import SwypeFrame.*;

public class Interpreter {
	TrieNode trie = new TrieNode();
	private final static int X_REL = -190;
	Map<Point2D, Character> cordLetter = new HashMap<Point2D, Character>() { {
		put(new Point2D.Double(34.0,  434.5+X_REL), 'q'); 
		put(new Point2D.Double(110.0, 434.5+X_REL), 'w'); 
		put(new Point2D.Double(182.0, 434.5+X_REL), 'e'); 
		put(new Point2D.Double(254.0, 434.5+X_REL), 'r'); 
		put(new Point2D.Double(326.0, 434.5+X_REL), 't'); 
		put(new Point2D.Double(398.0, 434.5+X_REL), 'y'); 
		put(new Point2D.Double(470.0, 434.5+X_REL), 'u'); 
		put(new Point2D.Double(542.0, 434.5+X_REL), 'i'); 
		put(new Point2D.Double(614.0, 434.5+X_REL), 'o'); 
		put(new Point2D.Double(686.0, 434.5+X_REL), 'p'); 
		put(new Point2D.Double(758.0, 434.5+X_REL), 'å'); 
		put(new Point2D.Double(34.0,  527.5+X_REL), 'a'); 
		put(new Point2D.Double(110.0, 527.5+X_REL), 's'); 
		put(new Point2D.Double(182.0, 527.5+X_REL), 'd'); 
		put(new Point2D.Double(254.0, 527.5+X_REL), 'f'); 
		put(new Point2D.Double(326.0, 527.5+X_REL), 'g'); 
		put(new Point2D.Double(398.0, 527.5+X_REL), 'h'); 
		put(new Point2D.Double(470.0, 527.5+X_REL), 'j'); 
		put(new Point2D.Double(542.0, 527.5+X_REL), 'k'); 
		put(new Point2D.Double(614.0, 527.5+X_REL), 'l'); 
		put(new Point2D.Double(686.0, 527.5+X_REL), 'ö'); 
		put(new Point2D.Double(758.0, 527.5+X_REL), 'ä'); 
		put(new Point2D.Double(34.0,  620.5+X_REL), 'z'); 
		put(new Point2D.Double(182.0, 620.5+X_REL), 'x'); 
		put(new Point2D.Double(254.0, 620.5+X_REL), 'c'); 
		put(new Point2D.Double(326.0, 620.5+X_REL), 'v'); 
		put(new Point2D.Double(398.0, 620.5+X_REL), 'b'); 
		put(new Point2D.Double(470.0, 620.5+X_REL), 'n'); 
		put(new Point2D.Double(542.0, 620.5+X_REL), 'm'); 
	}};
	Map<Character, Point2D> letterCord = new HashMap<Character, Point2D>();

	private static final double MAX_DISTANCE = 40.0;
	
	
	private static final String[] WORD_LIST = {
		"ramp",
		"axel",
		"mat",
		"sol",
		"hem",
		"het",
		"ras",
		"ramsa"
	};
	
	public static void main(String[] args) {
		File[] dir = new File("files").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		for(File file : dir){
			System.out.println(file.getPath());
			new Interpreter(file);
			break;
		}
	}
	SwypePoint[] curveData;
	SwypeFrame graphics;
	public Interpreter(File wordFile) {
		for (Map.Entry<Point2D, Character> entry : cordLetter.entrySet()) {
			letterCord.put(entry.getValue(), entry.getKey());
		}
		addWordList();
		try {
			curveData = new SwypeData(wordFile).getPoints();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphics = new SwypeFrame(wordFile);
		for (Point2D p : letterCord.values()) {
			graphics.markPoint(p);
		}
		findWord();
		optimizeWords();

		for (Map.Entry<String, Double> entry : words.entrySet()) {
			System.out.println(entry.getKey()+" "+entry.getValue());
		}
		
		graphics.setVisible(true);		
	}
	HashMap<String, Double> words;
	private void findWord() {
		System.out.println("Tries to find word!");
		words = new HashMap<String, Double>();
		for (LetterPriority lp : findFirstLetter()) {
			if (!trie.hasChild(lp.letter))
				continue;
			TrieNode firstNode = trie.getChild(lp.letter);
			findWord(firstNode, 0, 0, lp.priority);
		}
	}
	
	private void optimizeWords() {
		
	}
	private void findWord(TrieNode cNode, int index, int letterIndex, double value) {
		Point2D lPoint = letterCord.get(cNode.getChar());
		if (cNode.hasWord()) {
			if (curveData[curveData.length-1].distance(lPoint)<MAX_DISTANCE) {
				words.put(cNode.getWord(), value/(letterIndex+1));
			}
		}
		int nextIndex = -1;
		for (int i=index; i<curveData.length; i++) {
			SwypePoint point = curveData[i];
			if (point.distance(lPoint)<MAX_DISTANCE) {
				nextIndex = i;
				break;
			}
		}
		if (nextIndex==-1)
			return;
		for (TrieNode node : cNode) {
			findWord(node, nextIndex, letterIndex+1, value+curveData[nextIndex].distance(lPoint));
		}
	}
	
	public void printTrie(TrieNode root) {
		for (TrieNode node : root) {
			if (node.word != null)
				System.out.println(node.getWord());
			printTrie(node);
		}
	}
	
	public void addWordList() {
		for (String word : WORD_LIST) {
			trie.addWord(word);
		}
	}
	
	public List<LetterPriority> findFirstLetter() {
		ArrayList<LetterPriority> letters = new ArrayList<LetterPriority>();
		for (Map.Entry<Point2D, Character> entry : cordLetter.entrySet()) {
			LetterPriority lp = new LetterPriority(entry.getValue(), entry.getKey().distance(curveData[0]));
			if (lp.priority<MAX_DISTANCE)
				letters.add(lp);
		}
		Collections.sort(letters);
		return letters;
	}
	
	public int findLetter(char c, int index) {
		return 0;
	}
	
	private class LetterPriority implements Comparable<LetterPriority>{
		char letter;
		double priority;
		public LetterPriority(char letter, double priority) {
			this.letter = letter;
			this.priority = priority;
		}
		@Override
		public int compareTo(LetterPriority o) {
			return (int)Math.signum(priority-o.priority);
		}
	}
	
}

