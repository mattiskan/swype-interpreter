package TrieSwype;
import java.util.*;

public class TestResult {
	ArrayList<WordResult> allWords = new ArrayList<WordResult>();
	ArrayList<WordResult> correctWord = new ArrayList<WordResult>();
	ArrayList<WordResult> failedWord = new ArrayList<WordResult>();
	ArrayList<WordResult> wrongWord = new ArrayList<WordResult>();
	
	public void addCorrectWord(WordResult wr) {
		allWords.add(wr);
		correctWord.add(wr);
	}
	
	public void addFailedWord(WordResult wr) {
		allWords.add(wr);	
		failedWord.add(wr);	
	}
	public void addWrongWord(WordResult wr) {
		allWords.add(wr);	
		wrongWord.add(wr);	
	}
	
	static public class WordResult {
		String realWord;
		String otherWord;
		double ratio;
		public WordResult(String realWord, String otherWord) {
			this.realWord = realWord;
			this.otherWord = otherWord;
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
