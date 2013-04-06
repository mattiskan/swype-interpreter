package PermutationSwype;

import java.io.File;
import java.io.FileFilter;
import java.util.PriorityQueue;
import java.util.Scanner;

import SwypeFrame.SwypeData;
import Util.TestingWordList;



public class Interpreter {
	private PriorityQueue<WordComb> executionOrder = new PriorityQueue<WordComb>();
	public static void main(String[] args) {
		File[] dir = new File("files").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		
		new Interpreter(dir[0]);
	}
	Scanner sc = new Scanner(System.in);
	
	public Interpreter(File file){
		SwypeData sd = new SwypeData(file);
		System.out.println(file.getName());
		Curve curve = new Curve(sd);
				
		WordComb current = new WordComb(curve.curveData, curve.getTurns());
		System.err.println(current.word);
		while(!TestingWordList.contains(current.getNextWord())){
			executionOrder.add(current.nextPerm());
			executionOrder.add(current);
			vardump();
			current = executionOrder.poll();
			sc.next();
		}
		System.out.println("Found word: " + current.getNextWord());
	}
	
	private void vardump(){
		int i = 0;
		PriorityQueue<WordComb> temp = new PriorityQueue<WordComb>();
		while (!executionOrder.isEmpty()) {
			WordComb wc =  executionOrder.poll();
			System.out.println(i++ + ": "+ wc.word + " - " + wc.priority() + ", " + wc.penalty);
			temp.add(wc);
		}
		executionOrder = temp;
	}
}

