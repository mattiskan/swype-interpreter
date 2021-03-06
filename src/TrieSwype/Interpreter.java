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
import PermutationSwype.Turn;
import SwypeFrame.*;

import static Util.CharacterMap.*;

public class Interpreter {
	
	public static void main(String[] args) {
		File[] dir = new File("demofiles").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		
		Interpreter inter = new Interpreter("files/ordlista-stor.txt");
		TestConfig config = new TestConfig();
		for(File file : dir){
			new Curve(file);
			Map<Double, String> result = inter.Interpret(file, config);
			int i =0;
			System.out.println(file.getName()+":");
			System.out.println("----------------------");
			for (Map.Entry<Double, String> entry : result.entrySet()) {
				if(i++ >= 4){
					break;
				}
				System.out.println(entry.getValue());
			}
			System.out.println();
		}	
	}
	
	TrieNode trie = new TrieNode();
	//private static final double MAX_DISTANCE = 80.0;
	TestConfig config;
	

	//boolean showGraphics;
	SwypePoint[] curveData;
	SwypeFrame graphics;
	SwypeData data;
	public Interpreter(String wordListFile) {
		addWordList(wordListFile);
	
	}
	
	public Map<Double, String> Interpret(File swypeFile, TestConfig config) {
		this.config = config;
		data = new SwypeData(swypeFile);
		curveData = data.getPoints();
		if (config.showGraphics) {
			graphics = new SwypeFrame(swypeFile);
		}
		findWord();
		if (config.curveDistance || config.optimizeWords)
			optimizeWords();
		
		TreeMap<Double, String> sortedWords = new TreeMap<Double, String>();
		for (Map.Entry<String, Double> entry : words.entrySet()) {
			sortedWords.put(entry.getValue(), entry.getKey());
		}
		int counter = 0;
		/*for (Map.Entry<Double, String> entry : sortedWords.entrySet()) {
			System.out.println(entry.getValue()+" "+entry.getKey());
			if (counter++>20)
				break;			
		}*/
		if (config.showGraphics)
			graphics.setVisible(true);
		return sortedWords;
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
		double lastBestDist = 0;
		for (int x=0; x<word.length()-1; x++) {
			char c = word.charAt(x);
			char c2  = word.charAt(x+1);
			double bestDis = Integer.MAX_VALUE;
			for (; i<curveData.length; i++) {
				double dis1 = curveData[i].distance(toCord.get(c));
				double dis2 = curveData[i].distance(toCord.get(c2));
				if (dis1<bestDis) {
					bestDis = dis1;
					letterPos[x] = curveData[i];
					letterIndex[x] = i;
				}
				if (c==c2 && x+2 < word.length()) {
					char c3  = word.charAt(x+2);
					double dis3 = curveData[i].distance(toCord.get(c3));
					if (x==0 || bestDis<config.maxDistance && dis3<=dis1) {
						letterPos[x+1] = letterPos[x];
						letterIndex[x+1] = letterIndex[x];
						total += bestDis;
						x++;
						break;
					}
				} else if (x==0 || bestDis<config.maxDistance && dis2<=dis1)
					break;
			}
			if (x>1 && word.charAt(x-1) != c) {
				total -= lastBestDist * ((c==c2)?2:1);
				int to = letterIndex[x-1];
				for (int z=letterIndex[x]; z>to; z--) {
					char c3 = word.charAt(x-1);
					double dist1 = curveData[z].distance(toCord.get(c3));
					if (dist1<lastBestDist) {
						lastBestDist = dist1;
						letterPos[x-1] = curveData[z];
						letterIndex[x-1] = z;
					}
				}
				total += lastBestDist * ((c==c2)?2:1);
			}
			lastBestDist = bestDis;
			total += bestDis;
		}
		
		double bestDis = Integer.MAX_VALUE;
		for (i=curveData.length-1;i<curveData.length; i++) {
			char c= word.charAt(word.length()-1);
			double dis1 = curveData[i].distance(toCord.get(c));
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
			} else if (config.showGraphics && word.equals(data.getWord())) {
				graphics.markPoint(letterPos[i], word.charAt(i));
			}
		}
		if (config.curveDistance) {
			if (!config.optimizeWords)
				total = 0;
			total += checkDistance(word, letterPos, letterIndex);
			if (!config.optimizeWords)
				return total/word.length()/config.maxDistance;
		}
		//total += findCurves(word, letterPos, letterIndex);
		return ((total+bestDis)/word.length())/((config.curveDistance)?config.maxDistance*2:config.maxDistance);
	}
	
	private double findCurves(String word, Point2D[] letterPos, int[] letterIndex) {
		Curve curve = new Curve(data);
		Turn[] turns = curve.getTurns();
		int count = 2;
		for (Point2D turnPoint : turns) {
			for (Point2D lPos : letterPos) {
				if (lPos != null && turnPoint.distance(lPos)<30) {
					count++;
					break;
				}
			}
		}//(count/turns.length)*config.maxDistance/4+
		return  ((word.length()-2<turns.length)?config.maxDistance/4:0);
	}
	
	private double checkDistance(String word, Point2D[] letterPos, int[] letterIndex) {

		double error = 0;
		for (int i=1; i<word.length();i++) {
			double lineDist = data.linearDist(letterIndex[i-1], letterIndex[i]);
			double curvDist = data.curveDist(letterIndex[i-1], letterIndex[i]);
			if (curvDist>1.2*lineDist && lineDist>5) {
				error += curvDist/(1.2*lineDist);
			}
		}
		error /= word.length();
		return error*config.maxDistance;
	}
	
	
	private void findWord(TrieNode cNode, int index, int letterIndex, double value) {
		Point2D lPoint = toCord.get(cNode.getChar());
		if (cNode.hasWord()) {
			if (curveData[curveData.length-1].distance(lPoint)<config.maxDistance) {
				words.put(cNode.getWord(), value/(letterIndex+1)/config.maxDistance);
			}
		}
		int nextIndex = -1;
		for (int i=index; i<curveData.length; i++) {
			SwypePoint point = curveData[i];
			if (point.distance(lPoint)<config.maxDistance) {
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
			if ("qwertyuiopåasdfghjklöäzxcvbnm".indexOf(str.charAt(i))==-1)
				return false;
		}
	    return true; 
	}
	public void addWordList(String wordListFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(wordListFile));
			String line = null;
			while ((line = in.readLine())!=null) {
				if (checkNormalChar(line)) {
					trie.addWord(line);
				}
			}
		} catch (Exception e) {
			System.err.println("Error when reading:");
			e.printStackTrace();
		}
	}
	
	public List<LetterPriority> findFirstLetter() {
		ArrayList<LetterPriority> letters = new ArrayList<LetterPriority>();
		for (Map.Entry<Point2D, Character> entry : toLetter.entrySet()) {
			LetterPriority lp = new LetterPriority(entry.getValue(), entry.getKey().distance(curveData[0]));
			if (lp.priority<config.maxDistance)
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

