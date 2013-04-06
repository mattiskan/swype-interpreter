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
	TreeSet<PriorityLetterHolder> segments;
	PriorityLetterHolder bestPriority;
	double penalty = 1.0;
	
	final String word;

	public WordComb(SwypeData swypeData, Turn[] turns){
		this.data = swypeData;
		this.turns = turns;
		turnLetters = new PriorityLetterHolder[turns.length];
//		addedSegmentLetters = (LinkedList<Character>[]) new LinkedList[turns.length-1];
//		for(int i = 0; i < addedSegmentLetters.length; i++){
//			addedSegmentLetters[i] = new LinkedList<Character>();
//		}
		
		segments =  new TreeSet<PriorityLetterHolder>();
		
		populateTurnLetters();
		bestPriority = turnLetters[0];
		populateSegments();	
		
		updateBestPriority();
		
		this.word = getNextWord();
		//while(r());
	}
	
	private WordComb(PriorityLetterHolder[] turnLetters, TreeSet<PriorityLetterHolder> segments, double penalty, String word){
		this.word = word;
		this.turnLetters=turnLetters;
		this.segments=segments;
//		this.addedSegmentLetters = addedSegmentLetters;
		bestPriority = turnLetters[0];
		updateBestPriority();
		
		
		this.penalty = penalty;
		
	}
	
	public String getNextWord(){
		StringBuilder sb = new StringBuilder();
		Iterator<PriorityLetterHolder> it = segments.iterator();
		for(int i = 0; i < turnLetters.length; i++){
			sb.append(turnLetters[i].peekNextLetter());
			
			if(i+1 == turnLetters.length)
				break;

			PriorityLetterHolder p = it.next();
			
			if(p.peekNextLetter() != ' ')
				sb.append(p.peekNextLetter());
			else
				sb.append('-');
			
		}
		//System.out.println(sb.toString());
		return sb.toString();
	}
	
	private void updateBestPriority() {
		for(PriorityLetterHolder p : turnLetters){
			if(bestPriority.peekNextPriority() < p.peekNextPriority()){
				bestPriority = p;
			}
		}
		
		if(bestPriority.peekNextPriority() < segments.first().peekNextPriority()){
			bestPriority = segments.first();
		}
	}

	private void populateSegments(){
		for(int i = 0; i < turns.length-1; i++){
			PriorityLetterHolder p = populateSegment(i);
			
			if(segments.contains(p))
				System.out.println("ERROR :/");
			else 
				segments.add(p);
		}
	}
	
	private void populateTurnLetters() {
		for(int i = 0; i < turnLetters.length; i++){
			turnLetters[i] = new PriorityLetterHolder(getCharDistances(data.getPoint(turns[i].index)), -1);
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
		
		if(bestPriority.segmentIndex != -1){
			segments.remove(bestPriority);
			bestPriority.pollNextLetter();
			segments.add(bestPriority);
			
			return new WordComb((PriorityLetterHolder[])ObjectCopy.copy(turnLetters), (TreeSet<PriorityLetterHolder>)ObjectCopy.copy(segments), penalty*1.2, getNextWord());
		} else {
			bestPriority.pollNextLetter();
			return new WordComb((PriorityLetterHolder[])ObjectCopy.copy(turnLetters), (TreeSet<PriorityLetterHolder>)ObjectCopy.copy(segments), penalty, getNextWord());
		}
	}
	
	public double priority() {
		updateBestPriority();
		return bestPriority.peekNextPriority() * penalty;
	}
	
	@Override
	public int compareTo(WordComb o) {
		return  priority() < o.priority()? -1:1;
	}

}