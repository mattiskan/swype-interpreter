package PermutationSwype;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import SwypeFrame.*;

public class Curve {
	public static void main(String[] args) {
		File[] dir = new File("files").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".json");
			}
		});
		for(File file : dir){
			//System.out.println(file.getPath());
			new Curve(file);
		}	
	}
	
	SwypeData curveData;
	ArrayList<Turn> turns = new ArrayList<Turn>();
	
	SwypeFrame graphics;	
	
	public Curve(File wordFile){
		try {
			curveData = new SwypeData(wordFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphics = new SwypeFrame(wordFile);
		
		findTurns(50, Math.PI/1.3);
		
		
		graphics.setVisible(true);		
	}
	
	private void findTurns(int radius, double threshold){		
		int a = 0;
		int b = nPixlesAhead(0, radius);
		int c = getNextPoint(a, b);
		
		while(c != -1){
			double angle = calcAngle(a, b, c);
			if(angle < threshold){
				b = improveTurn(a,b,c);
				turns.add(new Turn(a, c, angle));
				graphics.markPoint(curveData.getPoint(b));
				//graphics.markPoint(curveData.getPoint(b), 'a');
				b=nPixlesAhead(b, 30);
				//graphics.markPoint(curveData.getPoint(b), 'b');
			}
			a = b;
			b = nPixlesAhead(b, radius);
			c = getNextPoint(a, b);
		}
	}
	
	private int improveTurn(int a, int b, int c){
		final int range = 20;
		a = nPixlesBack(a, range);		
		b = nPixlesBack(b, range);
		c = nPixlesBack(c, range);
		
		double bestAngle = Double.MAX_VALUE;
		int bestMiddle = 0;
		for(int i=-5; i < 5; i++){
			double angle = calcAngle(a, b, c);
			if(angle < bestAngle){
				bestAngle = angle;
				bestMiddle = b;
			}
			a = nPixlesAhead(a, 5);
			b = nPixlesAhead(b, 5);
			c = nPixlesAhead(c, 5);	
		}
		return bestMiddle;
	}
	
	private int findTurnMiddle(int a, int c) {
		int middle = -1;
		double bestAngle = Double.MAX_VALUE;
		for(int i = a; i < c; i++){
			double angle = calcAngle(a, i, c);
			if(angle< bestAngle){
				bestAngle = angle;
				middle = i;
			}
		}
		return middle;
	}
	
	private boolean takeTurns = true;
	private int getNextPoint(int a, int b){
		double goal = curveData.curveDist(a, b);
		
	    int c = b-1;
		double current = 0;
		
		do {
			if(c+2 >= curveData.size())
				return -1;
			c++;
			current += curveData.curveDist(c, c+1);
		} while(goal - current > 0);
		
		takeTurns = !takeTurns;
		
		return c + (takeTurns? 1:0);
	}
	
	private int nPixlesAhead(int point, int n){
		double sum = 0;
		int i;
		for(i = point; sum <= n && i+1 < curveData.size(); i++){
			sum += curveData.curveDist(i, i+1);
		}
		return i;
	}
	
	private int nPixlesBack(int point, int n){
		double sum = 0;
		int i;
		for(i = point; sum <= n && i > 0 ; i--){
			sum += curveData.curveDist(i-1, i);
		}
		return i;
	}

	
	private double calcAngle(int a, int b, int c){
		double ab = curveData.linearDist(a, b);
		double bc = curveData.linearDist(b, c);
		double ac = curveData.linearDist(a, c);
		
		//cossinussatsen 0-π		
		return Math.acos((ab*ab + bc*bc -ac*ac)/(2*ab*bc)) % Math.PI;
	}
	
}


class Turn {
	int a, b, c;
	double sharpness;
	public Turn(int a, int c, double sharpness) {
		this.a = a;
		this.b = a+c/2;
		this.c = b;
		
		this.sharpness = sharpness;
	}
	
}