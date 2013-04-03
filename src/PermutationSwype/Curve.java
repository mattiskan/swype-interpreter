package PermutationSwype;

import java.awt.geom.Point2D;

public class Curve {
	
	
	public Curve(String wordFile){
		
	}
	
	private void FindTurns(){
		
		
	}
	
	/**
	 *  Uppskatta hur mycket de tre punkterna "svänger"
	 *  Mer info:
	 *  https://www.mathworks.com/matlabcentral/answers/57194
	 */
	private double calculateTurn(Point2D p1, Point2D p2, Point2D p3 ){
		double x1 = p1.getX(); double x2 = p2.getX(); double x3 = p3.getX();
		double y1 = p1.getY(); double y2 = p2.getY(); double y3 = p3.getY();
		
		return 2*Math.abs((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1)) / 
				Math.sqrt(((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))*((x3-x1)*(x3-x1)+(y3-y1)*(y3-y1))*((x3-x2)*(x3-x2)+(y3-y2)*(y3-y2)));
	}
	
	
}


class Corner {
	Point2D.Float startPt, middlePt, endPt;
	
}

class Segment {
	Point2D.Float startPt, middlePt, endPt;
	
}