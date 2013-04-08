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

import static Util.CharacterMap.*;

public class Interpreter {
	TrieNode trie = new TrieNode();
	private static final double MAX_DISTANCE = 80.0;
	
	
	private final static double NOT_FOUND = -1;
	public static void main(String[] args) {
		File[] dir = new File("files").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith("0.json");
			}
		});
		Interpreter inter = new Interpreter("files/ordlista-stor.txt");
		for(File file : dir){
			//System.out.println("\n"+file.getPath());
			Map<Double, String> result = inter.Interpret(file, false);

			String realWord = inter.data.getWord();
			
			double realWordValue = -1;
			double failWordValue = -1;
			String failWord = null;
			http://mattiskan.se
			for (Map.Entry<Double, String> entry : result.entrySet()) {
				if (entry.getValue().equals(realWord)) {
					realWordValue = entry.getKey();
				} else if(failWord==null) {
					failWord = entry.getValue();
					failWordValue = entry.getKey();
				}
				if (realWordValue != NOT_FOUND  && failWordValue != NOT_FOUND){
					break;
				}
			}
			
			if(realWordValue == NOT_FOUND && !inter.trie.checkWord(realWord)){
				continue;
			}
			
			
			if (realWordValue != NOT_FOUND  && failWordValue != NOT_FOUND) {
				double kvot = realWordValue/failWordValue;
				System.out.printf("%-25s %2.5f   %s\n", realWord+":", kvot, (kvot<1)?"PASS": "FAILED: "+ failWord);
			} else if (realWordValue == NOT_FOUND  && failWordValue == NOT_FOUND) {
				System.out.printf("%-25s%11s%s\n", realWord+":", "",  "FAILED: not found");
			} else if (realWordValue == NOT_FOUND) {
				System.out.printf("%-25s%11s%s%s\n", realWord+":","",  "FAILED:", failWord);
			} else {
				System.out.printf("%-25s%11s%s\n", realWord+":", "", "PASS");
			}
		}
	}
	
	
	SwypePoint[] curveData;
	SwypeFrame graphics;
	SwypeData data;
	public Interpreter(String wordListFile) {
		addWordList(wordListFile);
	
	}
	
	public Map<Double, String> Interpret(File swypeFile, boolean showGraphics) {
		data = new SwypeData(swypeFile);
		curveData = data.getPoints();
		if (showGraphics) {
			graphics = new SwypeFrame(swypeFile);
		}
		findWord();
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
		if (showGraphics)
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
					if (x==0 || bestDis<MAX_DISTANCE && dis3<=dis1) {
						letterPos[x+1] = letterPos[x];
						letterIndex[x+1] = letterIndex[x];
						total += bestDis;
						x++;
						break;
					}
				} else if (x==0 || bestDis<MAX_DISTANCE && dis2<=dis1)
					break;
			}
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
			}
		}
		total += checkDistance(word, letterPos, letterIndex);
		return (total+bestDis)/word.length();
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
		return error*MAX_DISTANCE;
	}
	
	
	private void findWord(TrieNode cNode, int index, int letterIndex, double value) {
		Point2D lPoint = toCord.get(cNode.getChar());
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

