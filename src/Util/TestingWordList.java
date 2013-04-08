package Util;

import java.awt.geom.Point2D;
import java.io.ObjectOutputStream.PutField;
import java.util.*;

public class TestingWordList {
	private static final HashSet<String> words;
	
	static {
		words = new HashSet<String>(){ {
			add("rasmp");
			add("axel");
			add("mat");
			add("sol");
			add("sadism");
		} };
	}
	public static boolean contains(String word){
		return words.contains(word);
	}
	
}
