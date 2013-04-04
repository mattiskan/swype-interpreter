package TrieSwype;

import java.util.*;

public class Interpreter {
	TrieNode trie = new TrieNode();
	private static final String[] WORD_LIST = {
		"ramp",
		"axel",
		"mat",
		"sol",
		"hem"
	};
	public Interpreter() {
		addWordList();
	}
	
	public void addWordList() {
		for (String word : WORD_LIST) {
			trie.addWord(word);
		}
	}
	
}

