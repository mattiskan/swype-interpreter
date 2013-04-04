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
			System.out.println(file.getPath());
			new Curve(file);
		}	
	}
	
	SwypePoint[] curveData;
	ArrayList<Turn> turns = new ArrayList<Turn>();
	
	SwypeFrame graphics;	
	
	public Curve(File wordFile){
		try {
			curveData = new SwypeData(wordFile).getPoints();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		graphics = new SwypeFrame(wordFile);
		
		findTurns();
		
		graphics.setVisible(true);		
	}
	
	private void findTurns(){
		final int TURN_RADIUS = 50;
		final double TURN_THRESHOLD = Math.PI / 1.4;
		
		int a = 0;
		int b = nPixlesAhead(0, TURN_RADIUS);
		int c = getNextPoint(a, b);
		
		while(c != -1){
			double angle = calcAngle(a, b, c);
			if(angle < TURN_THRESHOLD){
				turns.add(new Turn(a, c, angle));
				graphics.markChar(curveData[b],'x');
			}
			a = b;
			b = c;
			c = getNextPoint(a, b);
		}
	}
	
	private int getNextPoint(int a, int b){
		double goal = curveDist(a, b);
		
	    int c = b-1;
		double current = 0;
		
		do {
			if(c+2 >= curveData.length)
				return -1;
			c++;
			current += curveDist(c, c+1);
		} while(goal - current > 0);
		return c+1;
	}
	
	private int nPixlesAhead(int point, int n){
		double sum = 0;
		int i;
		for(i = point; sum <= n && i < curveData.length; i++){
			sum += curveDist(i, i+1);
		}
		
		return i;
	}

	private double curveDist(int from, int to){
		double sum=0;
		for(int i = from; i < to; i++){
			sum += curveData[i].distance(curveData[i+1]);
		}
		return sum;
	}
	
	private double calcAngle(int a, int b, int c){
		double ab = curveData[a].distance(curveData[b]);
		double bc = curveData[b].distance(curveData[c]);
		double ac = curveData[a].distance(curveData[c]);
		
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

class Segment {
	Point2D.Float startPt, middlePt, endPt;
	
}