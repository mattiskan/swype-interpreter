package TrieSwype;
import java.util.*;

public class TestResult {
	ArrayList<WordResult> allWords = new ArrayList<WordResult>();
	ArrayList<WordResult> correctWord = new ArrayList<WordResult>();
	ArrayList<WordResult> failedWord = new ArrayList<WordResult>();
	ArrayList<WordResult> wrongWord = new ArrayList<WordResult>();
	ArrayList<WordResult> topFour = new ArrayList<WordResult>();
	
	public void addCorrectWord(WordResult wr) {
		allWords.add(wr);
		correctWord.add(wr);
		topFour.add(wr);
		
	}
	
	public void addFailedWord(WordResult wr) {
		allWords.add(wr);	
		failedWord.add(wr);	
		if (wr.position<4)
			topFour.add(wr);
	}
	public void addWrongWord(WordResult wr) {
		allWords.add(wr);	
		wrongWord.add(wr);	
	}
	
	static public class WordResult implements Comparable<WordResult>{
		String realWord;
		String otherWord;
		double ratio;
		int position;
		public WordResult(String realWord, String otherWord, int wordPos) {
			this.realWord = realWord;
			this.otherWord = otherWord;
			this.position = wordPos;
		}
		@Override
		public int compareTo(WordResult o) {
			if (ratio>0)
				return (int)Math.signum(o.ratio - ratio);
			return (int)Math.signum(ratio - o.ratio);
		}
	}
	
	public int getTotalWordCount() {
		return allWords.size();
	}
	
	public int getCorectWordCount() {
		return correctWord.size();
	}
	
	public int getFailedWordCount() {
		return failedWord.size()+wrongWord.size();
	}
}
