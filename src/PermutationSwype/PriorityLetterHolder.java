package PermutationSwype;

import java.io.Serializable;
import java.util.Arrays;

import Util.CharacterMap;

public class PriorityLetterHolder implements Comparable<PriorityLetterHolder>, Serializable{
	
	Letter[] letters = new Letter[30];
	int i = 0;
	
	int segmentIndex;

	public PriorityLetterHolder(double[] charDistances, int segmentIndex){
		for(int ci = 0; ci < charDistances.length; ci++){
			letters[ci+1] = new Letter(CharacterMap.posToChar(ci), distanceToPriority(charDistances[ci]));
		}
		
		this.segmentIndex = segmentIndex;
		if(segmentIndex == -1){
			letters[0] = new Letter('¤', -1);
			Arrays.sort(letters);
		} else {
			Arrays.sort(letters, 1, letters.length);
			letters[0] = new Letter(' ', letters[1].priority);
		}		
	}
	
	
	public double peekNextPriority(){
		if(i == letters.length)
			return -1;
		return letters[i].priority * (segmentIndex ==-1 ? 1.5 : 1);
	}
	
	public char pollNextLetter(){
		System.out.println("POLL!");
		if(i == letters.length)
			return '\0';
		return letters[i++].character;
	}
	
	
	public char peekNextLetter(){
		if(letters[i].character == '¤')
			throw new NullPointerException("¤");
		
		if(i == letters.length)
			return '\0';
		return letters[i].character;
	}

	private double distanceToPriority(double distance){
		//System.out.println("prio: " + distance);
		return 1/distance;
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
			
			return this.priority < o.priority? 1: -1;
		}
	}

	@Override
	public int compareTo(PriorityLetterHolder o) {
		if(this == o)
			return 0;
		return peekNextPriority() < o.peekNextPriority()? 1 : -1;
	}
}
