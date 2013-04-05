package TrieSwype;

import java.util.ArrayList;
import java.util.Iterator;

class TrieNode implements Iterable<TrieNode> {
	char c;
	ArrayList<TrieNode> available = new ArrayList<TrieNode>();
	TrieNode[] children = new TrieNode[28];
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
	
	public boolean hasChild(char letter) {
		return children[letter-'a']!=null;
	}
	public TrieNode getChild(char letter) {
		return children[letter-'a'];
	}
	
	private void addWord(int letter, String word) {
		char cc = word.charAt(letter);
		TrieNode node = children[cc-'a'];
		boolean lastLetter = (letter==word.length()-1);
		if (node==null) {
			node = new TrieNode(cc, lastLetter ? word : null);
			children[cc-'a'] = node;
			available.add(node);
		}
		if (!lastLetter)
			node.addWord(letter+1, word);
	}
	
	public Iterator<TrieNode> iterator() {
		return available.iterator();
	}
}