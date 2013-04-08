package SwypeFrame;

import java.io.*;
import java.util.Iterator;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class SwypeData implements Iterable<SwypePoint> {
	private String word;
	private SwypePoint[] points;
	private double[] distances;
	
	public SwypeData(File f)  {
		try {
			JsonParser parser = new JsonParser();
			JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f))));
			JsonObject container = parser.parse(reader).getAsJsonObject();
			word = container.get("word").getAsString();
			JsonArray pointArray = container.get("data").getAsJsonArray();
			points = new SwypePoint[pointArray.size()];
			distances = new double[pointArray.size()];
			for (int i=0; i<pointArray.size(); i++) {
				JsonObject p = pointArray.get(i).getAsJsonObject();
				double x = p.get("x").getAsDouble();
				double y = p.get("y").getAsDouble();
				long time = p.get("time").getAsLong();
				points[i] = new SwypePoint(x, y, time);
				if(i != 0){
					distances[i] = distances[i-1] + points[i-1].distance(points[i]);
					//System.out.println("Distance:" + distances[i]);
				}
			}
		} catch( IOException e){
			e.printStackTrace();
		}
	}
	
	public double curveDist(int from, int to){
		return distances[to] - distances[from];
	}
	public double linearDist(int from, int to){
		return points[from].distance(points[to]);
	}
	
	public String getWord() {
		return word;
	}
	
	public SwypePoint getPoint(int i){
		return points[i];
	}
	
	public SwypePoint[] getPoints() {
		return points;
	}
	
	public int size(){
		return points.length;
	}
	
	@Override
	public Iterator<SwypePoint> iterator() {
		return new Iterator<SwypePoint>() {
			int current=0;
			@Override
			public boolean hasNext() {
				return current<points.length;
			}
			@Override
			public SwypePoint next() {
				return points[current++];
			}
			@Override
			public void remove() {
				throw new NullPointerException();
			}
		};
	}
}