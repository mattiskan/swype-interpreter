package PermutationSwype;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

import SwypeFrame.SwypeData;
import Util.TestingWordList;



public class Interpreter {
	private PriorityQueue<WordComb> executionOrder = new PriorityQueue<WordComb>();
	private HashSet<String> testedWords = new HashSet<String>(1000000);
	
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
	
	
	WordComb current, next;
	public Interpreter(File file){
		SwypeData sd = new SwypeData(file);
		System.out.println(file.getName());
		Curve curve = new Curve(sd);
				
		current = new WordComb(curve.curveData, curve.getTurns());
		executionOrder.add(current);
		int debug = 1;
		int i = 0;
		String lastWord = "";
		vardump(debug);
		do {
			i++;
			current = executionOrder.poll();
			next = current.nextPerm();
			executionOrder.add(current);
			if(next.priority() != 0){
				executionOrder.add(next);
			}
			
			if(lastWord.equals(next.word) || i == 144){
				lastWord = "seccond";
				System.out.println(i);
				vardump(4);
			}	else
				lastWord = next.word;
			
			vardump(0);
		}while(!TestingWordList.contains(next.word));
		
		System.err.println("Found word: " + next.word + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11");
	}
	
	private void vardump(int lvl) {
		switch (lvl) {
		case 4:
			current.showValues();
		case 3:
			int i = 0;
			PriorityQueue<WordComb> temp = new PriorityQueue<WordComb>();
			while (!executionOrder.isEmpty() && i < 20) {
				WordComb wc =  executionOrder.poll();
				System.out.println(i++ + ": "+ wc.word + " - " + wc.priority() + ", " + wc.penalty);
				temp.add(wc);
			}
			executionOrder = temp;
			sc.next();
		case 2:
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {}
		case 1:
			if(next!= null)
				System.out.println(next.word + " "+ next.priority());
		}
	}
}

