package TrieSwype;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

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
		put(new Point2D.Double(110.0,  620.5+X_REL), 'z'); 
		put(new Point2D.Double(182.0, 620.5+X_REL), 'x'); 
		put(new Point2D.Double(254.0, 620.5+X_REL), 'c'); 
		put(new Point2D.Double(326.0, 620.5+X_REL), 'v'); 
		put(new Point2D.Double(398.0, 620.5+X_REL), 'b'); 
		put(new Point2D.Double(470.0, 620.5+X_REL), 'n'); 
		put(new Point2D.Double(542.0, 620.5+X_REL), 'm'); 
	}};
	Map<Character, Point2D> letterCord = new HashMap<Character, Point2D>();

	private static final double MAX_DISTANCE = 100.0;
	
	
	private static final String[] WORD_LIST = {
		"ramp",
		"axel",
		"mat",
		"sol",
		"hem",
		"het",
		"ras",
		"ramsa",
		"mattis"
	};
	
	public static void main(String[] args) {
		File[] dir = new File("files").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		for(File file : dir){
			System.out.println("\n"+file.getPath());
			new Interpreter(file);
		}
		//System.exit(0);
	}
	SwypePoint[] curveData;
	SwypeFrame graphics;
	SwypeData data;
	public Interpreter(File wordFile) {
		for (Map.Entry<Point2D, Character> entry : cordLetter.entrySet()) {
			letterCord.put(entry.getValue(), entry.getKey());
		}
		addWordList();
		
		data = new SwypeData(wordFile);
		curveData = data.getPoints();
		
		graphics = new SwypeFrame(wordFile);
		for (Point2D p : letterCord.values()) {
			graphics.markPoint(p);
		}
		findWord();
		optimizeWords();
		
		TreeMap<Double, String> sortedWords = new TreeMap<Double, String>();
		for (Map.Entry<String, Double> entry : words.entrySet()) {
			sortedWords.put(entry.getValue(), entry.getKey());
		}
		int counter = 0;
		for (Map.Entry<Double, String> entry : sortedWords.entrySet()) {
			System.out.println(entry.getValue()+" "+entry.getKey());
			if (counter++>2)
				break;			
		}
		graphics.setVisible(true);		
	}
	HashMap<String, Double> words;
	private void findWord() {
		words = new HashMap<String, Double>();
		for (LetterPriority lp : findFirstLetter()) {
			if (!trie.hasChild(lp.letter))
				continue;
			TrieNode firstNode = trie.getChild(lp.letter);
			findWord(firstNode, 0, 0, lp.priority);
		}
	}
	
	private void optimizeWords() {
		for (String word : words.keySet()) {
			words.put(word, optimizeWord(word));
		}
	}
	
	private double optimizeWord(String word) {
		double total = 0;
		Point2D[] letterPos = new Point2D[word.length()];
		int[] letterIndex = new int[word.length()];
		int i=0;
		for (int x=0; x<word.length()-1; x++) {
			char c = word.charAt(x);
			char c2  = word.charAt(x+1);
			double bestDis = Integer.MAX_VALUE;
			for (; i<curveData.length; i++) {
				double dis1 = curveData[i].distance(letterCord.get(c));
				double dis2 = curveData[i].distance(letterCord.get(c2));
				//System.out.printf("1:%f 2:%f b:%f c1:%s c2:%s\n", dis1, dis2, bestDis, c, c2);

				if (dis1<bestDis) {
					bestDis = dis1;
					letterPos[x] = curveData[i];
					letterIndex[x] = i;
					//System.out.println(c);
				}
				if (bestDis<MAX_DISTANCE && dis2<dis1)
					break;
			}
			total += bestDis;
		}
		double bestDis = Integer.MAX_VALUE;
		for (i=curveData.length-10;i<curveData.length; i++) {
			char c= word.charAt(word.length()-1);
			double dis1 = curveData[i].distance(letterCord.get(c));
			if (dis1<bestDis) {
				bestDis = dis1;
				letterPos[word.length()-1] = curveData[i];
				letterIndex[word.length()-1] = i;
			}
		}
		for (i=0; i<word.length(); i++) {
			if (letterPos[i]==null) {
				total += 10000;
				break;
			}
			//graphics.markPoint(letterPos[i], word.charAt(i));
		}
		total += checkDistance(word, letterPos, letterIndex);
		return (total+bestDis)/word.length();
	}
	
	private double checkDistance(String word, Point2D[] letterPos, int[] letterIndex) {
		double error = 0;
		for (int i=1; i<word.length();i++) {
			double lineDist = data.linearDist(letterIndex[i-1], letterIndex[i]);
			double curvDist = data.curveDist(letterIndex[i-1], letterIndex[i]);
			if (curvDist>1.2*lineDist) {
				error += curvDist/(1.2*lineDist);
			}
		}
		error /= word.length();
		return error*MAX_DISTANCE;
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
	public boolean checkNormalChar(String str) {
		for (int i=0; i<str.length(); i++) {
			if (str.charAt(i)<'a' || str.charAt(i)>'z')
				return false;
		}
	    return true; 
	}
	public void addWordList() {
		for (String word : WORD_LIST) {
			trie.addWord(word);
		}
		try {
			BufferedReader in = new BufferedReader(new FileReader("files/ordlista.txt"));
			String line = null;
			while ((line = in.readLine())!=null) {
				if (checkNormalChar(line)) {
					trie.addWord(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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

