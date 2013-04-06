package PermutationSwype;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

import SwypeFrame.SwypeData;
import Util.*;

public class WordComb implements Comparable<WordComb>{
	
	SwypeData data;
	Turn[] turns;
	PriorityLetterHolder[] turnLetters;
//	LinkedList<Character>[] addedSegmentLetters;
	PriorityLetterHolder[] segments;
	PriorityLetterHolder bestPriority;
	double bestPriorityValue;
	double penalty = 1.0;
	
	final String word;

	public WordComb(SwypeData swypeData, Turn[] turns){
		this.data = swypeData;
		this.turns = turns;
		turnLetters = new PriorityLetterHolder[turns.length];
		
		segments =  new PriorityLetterHolder[turns.length-1];
		
		populateTurnLetters();

		populateSegments();
		
		bestPriority = turnLetters[0];
		updateBestPriority();
		
		this.word = generateWord();
		//while(r());
	}
	
	private WordComb(PriorityLetterHolder[] turnLetters, PriorityLetterHolder[] segments, double penalty, String word){
		this.word = word;
		this.turnLetters=turnLetters;
		this.segments=segments;
//		this.addedSegmentLetters = addedSegmentLetters;
		this.penalty = penalty;
	}
	
	public String generateWord(){
		return generateWord(turnLetters, segments);
	}
	
	public String generateWord(PriorityLetterHolder[] turnLetters, PriorityLetterHolder[] segments){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < turnLetters.length; i++){
			sb.append(turnLetters[i].peekCurrentLetter());
			
			if(i+1 == turnLetters.length)
				break;

			
			sb.append(segments[i].peekCurrentLetter());
			
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	

	
	private void updateBestPriority() {
		bestPriority = turnLetters[0];
		
		int count = 0;
		double avg = 0;
		for(PriorityLetterHolder p : turnLetters){
			if(p.isDead){
				bestPriorityValue = 0;
				return;
			}
				
			if(bestPriority.peekNextPriority() < p.peekNextPriority()){
				bestPriority = p;
			}
			//System.out.println("Turnletter " + p.peekNextLetter() + ": " + p.peekNextPriority());
			avg+= p.peekNextPriority();
			count++;
		}
		
		for(PriorityLetterHolder p : segments){
			if(p.isDead){
				bestPriorityValue = 0;
				return;
			}
			
			if(bestPriority.peekNextPriority() < p.peekNextPriority()){
				bestPriority = p;
			}
			
			avg += p.peekNextPriority();
			count++;
			//System.out.println("Segment " + p.peekNextLetter() + ": " + p.peekNextPriority());
		}		
		
		bestPriorityValue = avg / count;
	}
	
	public void showValues() {
		System.out.print(word);
		for(int i = 0; i < turnLetters.length; i++){
			PriorityLetterHolder p = turnLetters[i];
			System.out.print("\nT:   '" + p.peekCurrentLetter()+"' -> '"+p.peekNextLetter() + "' : " + p.peekNextPriority());
			if(p == bestPriority)
				System.out.print(" <-- next");
			if(i < segments.length){
				PriorityLetterHolder p2  = segments[i];
				System.out.print("\nS:   '" + p2.peekCurrentLetter()+"' -> '"+p2.peekNextLetter() + "' : " + p2.peekNextPriority());
				if(p2 == bestPriority)
					System.out.print(" <-- next");
			}
		}
		System.out.println();
	}
	

	private void populateSegments(){
		for(int i = 0; i < segments.length; i++){
			PriorityLetterHolder p = populateSegment(i);
			segments[i] = p;
		}
	}
	
	private void populateTurnLetters() {
		for(int i = 0; i < turnLetters.length; i++){
			double[] chars = getCharDistances(data.getPoint(turns[i].index));
			turnLetters[i] = new PriorityLetterHolder(chars , -1);
		}
	}

	private PriorityLetterHolder populateSegment(int segment) {
		double[] charDistances = getCharDistances(data.getPoint(segment));
		
		for(int i = segment+1; pointIsNextTurn(i, segment+1) ; i++) {
			double[] currentDistances = getCharDistances(data.getPoint(i));
			for(int c = 0; c < charDistances.length; c++){
				if(currentDistances[c] < charDistances[c]){
					charDistances[c] = currentDistances[c];
				}
			}
		}
//		if(segment == 0){
//			for(int i = 0; i < charDistances.length; i++){
//				System.out.println(CharacterMap.posToChar(i) + " " + charDistances[i]);
//			}
//		}
		return new PriorityLetterHolder(charDistances, segment);
	}
	
	private double[] getCharDistances(Point2D point){
		double[] charDistances = new double[29];
		for(Point2D letterPosition : CharacterMap.toLetter.keySet()) {
			int charPos = CharacterMap.charToPos(CharacterMap.toLetter.get(letterPosition));
			charDistances[charPos] = point.distance(letterPosition);
		}
		return charDistances;
	}
	
	private boolean pointIsNextTurn(int pointIndex, int nextTurnIndex){
		return data.getPoint(pointIndex) != data.getPoint(turns[nextTurnIndex].index);
	}
	
	@SuppressWarnings("unchecked")
	public WordComb nextPerm() {
		PriorityLetterHolder[] turnLetterCopy = (PriorityLetterHolder[])ObjectCopy.copy(turnLetters);
		PriorityLetterHolder[] segmentCopy = (PriorityLetterHolder[]) ObjectCopy.copy(segments);
		
		if(bestPriority.segmentIndex != -1){
			
			
			int i;
			for(i =0; i < segments.length; i++) {
				if(segments[i] == bestPriority)
					break;
			}
			segmentCopy[i].poll();
			

			bestPriority.strikeAhead();		
			
			WordComb result = new WordComb(turnLetterCopy, segmentCopy, penalty, generateWord(turnLetterCopy, segmentCopy));
			//penalty -= 0.05;
			
			return result;
		} else {
			
			int i;
			for(i =0; i < turnLetters.length; i++) {
				if(turnLetters[i] == bestPriority)
					break;
			}
			turnLetterCopy[i].poll();
			bestPriority.strikeAhead();
			
			
			System.out.println("my result:" + priority());
			WordComb result = new WordComb(turnLetterCopy , segmentCopy, penalty, generateWord(turnLetterCopy, segmentCopy));
			System.out.println("your:     "+ result.priority());
			//penalty -= 0.05;
			return result;
		}
	}
	
	public double priority() {
		updateBestPriority();
		return bestPriorityValue * penalty;
	}
	
	@Override
	public int compareTo(WordComb o) {
		return  priority() < o.priority()? 1:-1;
	}



}