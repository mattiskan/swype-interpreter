package PermutationSwype;

import SwypeFrame.*;

public class Turn extends SwypePoint{
	int index;
	
	double sharpness;
	
	public Turn(int index, SwypeData curve, double sharpness) {
		super(curve.getPoint(index));
		this.index = index;
		
		this.sharpness = sharpness;
	}
	
}