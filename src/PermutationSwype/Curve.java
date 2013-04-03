package PermutationSwype;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import SwypeFrame.*;

public class Curve {
	SwypePoint[] curveData;
	ArrayList<Turn> turns = new ArrayList<Turn>();
	
	
	
	
	public Curve(String wordFile){
		try {
			curveData = new SwypeData(new File("files/"+wordFile)).getPoints();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void findTurns(){
		final int TURN_RADIUS = 100;
		final int TURN_THRESHOLD = 0;
		
		int a = 0;
		int c = 1;
		
		while(true){
			//suitable distance between a and b:
			while(curveData[a].distanceTo(curveData[c]) < TURN_RADIUS){
				c++;
				if(c > curveData.length)
					return;
			}
			double sharpness = calculateTurn(a, c);
			if( sharpness> TURN_THRESHOLD){
				//it's a corner!
				turns.add(new Turn(a, c, sharpness));
			}
			a = c;
			c++;
		}		
	}
	
	
	
	/**
	 *  Uppskatta hur mycket de tre punkterna "svänger"
	 *  Mer info:
	 *  https://www.mathworks.com/matlabcentral/answers/57194
	 */
	private double calculateTurn(int a, int c ){
		double x1 = curveData[a].x;
		double y1 = curveData[a].y;
		
		int b = a+c/2;
		double x2 = curveData[b].x;
		double y2 = curveData[b].y;
		
		double x3 = curveData[c].x;
		double y3 = curveData[c].y;
		
		return 2*Math.abs((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1)) / 
				Math.sqrt(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))*((x3-x1)*(x3-x1)+(y3-y1)*(y3-y1))*((x3-x2)*(x3-x2)+(y3-y2)*(y3-y2)));
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