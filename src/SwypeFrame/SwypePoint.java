package SwypeFrame;

import java.awt.geom.Point2D;

public class SwypePoint extends Point2D {
	public final double x;
	public final double y;
	public final long time;
	public SwypePoint(double x, double y, long time) {
		this.x = x;
		this.y = y;
		this.time = time;
	}
	@Override
	public double getX() {
		// TODO Auto-generated method stub
		return x;
	}
	@Override
	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}
	@Override
	public void setLocation(double arg0, double arg1) {
		throw new RuntimeException("you failed!");
	}
	
	public double distanceTo(SwypePoint other){
		//Pythagoras fucking sats!
		double dx = x-other.x;
		double dy = y-other.y;
		return Math.sqrt(dx*dx + dy*dy);
	}
}
