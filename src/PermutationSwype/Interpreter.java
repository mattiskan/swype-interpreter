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
		while(!TestingWordList.contains(current.getCurrentWord())){
			vardump();
			executionOrder.add(current.nextPerm());
			executionOrder.add(current);
			current = executionOrder.poll();
			sc.next();
		}
		System.out.println("Found word: " + current.getCurrentWord());
	}
	
	private void vardump(){
		int i = 0;
		PriorityQueue<WordComb> temp = new PriorityQueue<WordComb>();
		while (!executionOrder.isEmpty()) {
			WordComb wc =  executionOrder.poll();
			System.out.println(i++ + ": "+ wc.getCurrentWord() + " - " + wc.priority());
			temp.add(wc);
		}
		executionOrder = temp;
	}
}

