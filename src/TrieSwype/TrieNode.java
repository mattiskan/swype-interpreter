package TrieSwype;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

class TrieNode implements Iterable<TrieNode> {
	char c;
	ArrayList<TrieNode> available = new ArrayList<TrieNode>();
	TrieNode[] children = new TrieNode[29];
	String word;
	/**
	 * Creates the root node
	 */
	public TrieNode() {
	}
	
	/**
	 * Creates a subnode
	 * @param c the char this node represents.
	 * @param word if this node is the last letter of a word then this
	 * should be the word. Else null.
	 */
	private TrieNode(char c, String word) {
		this.c = c;
		this.word = word;
	}
	/**
	 * Add a word, should only be used on the root node.
	 * @param word the word that should be added.
	 */
	public void addWord(String word) {
		addWord(0, word);
	}
	public boolean hasWord() {
		return word!=null;
	}
	public String getWord() {
		return word;
	}
	public char getChar() {
		return c;
	}
	
	private int letterValue(char letter) {
		switch (letter) {
		case 'å': return 26;
		case 'ä': return 27;
		case 'ö': return 28;
		default: return letter-'a';
		}
	}
	
	public boolean hasChild(char letter) {
		try {
			return children[letterValue(letter)]!=null;
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(letter);
			return false;
		}
	}
	public TrieNode getChild(char letter) {
		return children[letterValue(letter)];
	}

	private void addWord(int letter, String word) {
		try {
			char cc = word.charAt(letter);
			TrieNode node = children[letterValue(cc)];
			boolean lastLetter = (letter==word.length()-1);
			if (node==null) {
				node = new TrieNode(cc, lastLetter ? word : null);
				children[letterValue(cc)] = node;
				available.add(node);
			} else if (lastLetter) {
				node.word = word;
			}
			if (!lastLetter)
				node.addWord(letter+1, word);
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println(word.charAt(letter)+" "+word+" "+letter);
			e.printStackTrace();
			System.out.println("---");
			throw e;
		}
	}
	
	public Iterator<TrieNode> iterator() {
		return available.iterator();
	}
	public boolean checkWord(String word) {
		TrieNode next =children[letterValue(word.charAt(0))];
		if (next==null)
			return false;
		return next.checkWord(word, 0);
	}
	
	private boolean checkWord(String cWord, int pos) {
		if (pos==cWord.length()-1)
			return cWord.equals(word);
		TrieNode next =children[letterValue(cWord.charAt(pos+1))];
		if (next==null)
			return false;
		return next.checkWord(cWord, pos+1);
	}
}