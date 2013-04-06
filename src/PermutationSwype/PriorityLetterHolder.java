package PermutationSwype;

import java.io.Serializable;
import java.util.Arrays;

import Util.CharacterMap;

public class PriorityLetterHolder implements Comparable<PriorityLetterHolder>, Serializable{
	
	Letter[] letters = new Letter[30];
	int i, j = 0;
	
	int segmentIndex, pollcount = 0;
	boolean isDead = false;

	public PriorityLetterHolder(double[] charDistances, int segmentIndex){
		this.segmentIndex = segmentIndex;
		
		for(int ci = 0; ci < charDistances.length; ci++){
			letters[ci+1] = new Letter(CharacterMap.posToChar(ci), distanceToPriority(charDistances[ci]));
		}
		
		if(segmentIndex == -1){
			letters[0] = new Letter('¤', Double.MAX_VALUE);
			Arrays.sort(letters);
		} else {
			Arrays.sort(letters, 1, letters.length);
			letters[0] = new Letter(' ', 2.5);
		}		
	}
	
	
	public double peekNextPriority(){		
		if(j+1 >= letters.length){
			isDead = true;
			return 0;
		}
		if(segmentIndex == -1){
			return letters[0].priority / letters[j+1].priority;//*0.9 - 0.1*pollcount;
		} else {
			return letters[1].priority / letters[j+1].priority;//- 0.1*pollcount;
		}
	}

	public void strikeAhead() {
		j++;
	}
	
	public char poll(){
		pollcount++;
		if(i == letters.length){
			this.isDead = true;
			return '¤';
		}		
		
		char nextLetter = letters[i].character;
		i = ++j;
		return nextLetter;
	}
	
	
	public char peekNextLetter(){
		if(j + (segmentIndex==-1 ? 1:0) >= letters.length){
			isDead=true;
			return '¤';
		}
			
		return letters[j+1].character;
	}
	
	public char peekCurrentLetter() {
		if(j + (segmentIndex==-1 ? 1:0) >= letters.length){
			isDead=true;
			return '¤';
		}
		return letters[i].character;
	}

	private double distanceToPriority(double distance){
		return distance;
	}
	
	private class Letter implements Comparable<Letter>, Serializable{
		final char character;
		final double priority;
		public Letter(char character, double priority) {
			this.character = character;
			this.priority = priority;
		}
		@Override
		public int compareTo(Letter o) {
			if(this == o)
				return 0;
			
			return (this.priority < o.priority? -1: 1);
		}
	}

	@Override
	public int compareTo(PriorityLetterHolder o) {
		if(this == o)
			return 0;
		return (peekNextPriority() < o.peekNextPriority()? 1 : -1) * (segmentIndex == -1? -1 : 1);
	}

}
