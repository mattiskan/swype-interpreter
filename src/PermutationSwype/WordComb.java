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

	public WordComb(SwypeData swypeData, Turn[] turns){
		this.data = swypeData;
		this.turns = turns;
		turnLetters = new PriorityLetterHolder[turns.length];
//		addedSegmentLetters = (LinkedList<Character>[]) new LinkedList[turns.length-1];
//		for(int i = 0; i < addedSegmentLetters.length; i++){
//			addedSegmentLetters[i] = new LinkedList<Character>();
//		}
		
		segments =  new TreeSet<PriorityLetterHolder>(new Comparator<PriorityLetterHolder>() {
				@Override
				public int compare(PriorityLetterHolder o1, PriorityLetterHolder o2) {
					return o1.peekNextPriority() < o2.peekNextPriority()? -1 : 1;
				}
			}
		);
		
		populateTurnLetters();
		bestPriority = turnLetters[0];
		populateSegments();	
		
		updateBestPriority();
		//while(r());
	}
	
	public WordComb(PriorityLetterHolder[] turnLetters, TreeSet<PriorityLetterHolder> segments){
		this.turnLetters=turnLetters;
		this.segments=segments;
//		this.addedSegmentLetters = addedSegmentLetters;
		bestPriority = turnLetters[0];
		updateBestPriority();
		int i = bestPriority.segmentIndex;
		bestPriority.pollNextLetter();
	}
	
	public String getCurrentWord(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < turnLetters.length; i++){
			sb.append(turnLetters[i].peekNextLetter());
			
			if(i+1 == turnLetters.length)
				break;
			for(PriorityLetterHolder p : segments){
				if(p.peekNextLetter() != ' ')
					sb.append(p.peekNextLetter());
			}
		}
		System.out.println(sb.toString());
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
			segments.add(populateSegment(i));
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
	
	public WordComb nextPerm() {
		return new WordComb((PriorityLetterHolder[])ObjectCopy.copy(turnLetters), (TreeSet<PriorityLetterHolder>)ObjectCopy.copy(segments));
	}
	
	@Override
	public int compareTo(WordComb o) {
		updateBestPriority();
		o.updateBestPriority();
		return bestPriority.peekNextPriority() < o.bestPriority.peekNextPriority()? -1:1;
	}

}