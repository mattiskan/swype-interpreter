package Util;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class CharacterMap {
	public static final Map<Point2D, Character> toLetter;
	
	public static int charToPos(char c){
		c = Character.toLowerCase(c);
		switch (c) {
		case 'å':
			return 26;
		case 'ä':
			return 27;
		case 'ö':
			return 28;
		default:
			return c - 'a';
		}
	}
	
	public static char posToChar(int pos){
		switch (pos) {
		case 26:
			return 'å';
		case 27:
			return 'ä';
		case 28:
			return 'ö';
		default:
			return (char)('a'+pos);
		}
	}
	
	
	static {
		final int X_REL = -190;
		toLetter = new HashMap<Point2D, Character>() { {
				put(new Point2D.Double(34.0,  434.5+X_REL), 'q'); 
				put(new Point2D.Double(110.0, 434.5+X_REL), 'w'); 
				put(new Point2D.Double(182.0, 434.5+X_REL), 'e'); 
				put(new Point2D.Double(254.0, 434.5+X_REL), 'r'); 
				put(new Point2D.Double(326.0, 434.5+X_REL), 't'); 
				put(new Point2D.Double(398.0, 434.5+X_REL), 'y'); 
				put(new Point2D.Double(470.0, 434.5+X_REL), 'u'); 
				put(new Point2D.Double(542.0, 434.5+X_REL), 'i'); 
				put(new Point2D.Double(614.0, 434.5+X_REL), 'o'); 
				put(new Point2D.Double(686.0, 434.5+X_REL), 'p'); 
				put(new Point2D.Double(758.0, 434.5+X_REL), 'å'); 
				put(new Point2D.Double(34.0,  527.5+X_REL), 'a'); 
				put(new Point2D.Double(110.0, 527.5+X_REL), 's'); 
				put(new Point2D.Double(182.0, 527.5+X_REL), 'd'); 
				put(new Point2D.Double(254.0, 527.5+X_REL), 'f'); 
				put(new Point2D.Double(326.0, 527.5+X_REL), 'g'); 
				put(new Point2D.Double(398.0, 527.5+X_REL), 'h'); 
				put(new Point2D.Double(470.0, 527.5+X_REL), 'j'); 
				put(new Point2D.Double(542.0, 527.5+X_REL), 'k'); 
				put(new Point2D.Double(614.0, 527.5+X_REL), 'l'); 
				put(new Point2D.Double(686.0, 527.5+X_REL), 'ö'); 
				put(new Point2D.Double(758.0, 527.5+X_REL), 'ä'); 
				put(new Point2D.Double(34.0,  620.5+X_REL), 'z'); 
				put(new Point2D.Double(182.0, 620.5+X_REL), 'x'); 
				put(new Point2D.Double(254.0, 620.5+X_REL), 'c'); 
				put(new Point2D.Double(326.0, 620.5+X_REL), 'v'); 
				put(new Point2D.Double(398.0, 620.5+X_REL), 'b'); 
				put(new Point2D.Double(470.0, 620.5+X_REL), 'n'); 
				put(new Point2D.Double(542.0, 620.5+X_REL), 'm'); 
			} };
	}
}
