package SwypeFrame;

import java.io.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class SwypeData {
	public String word;
	public SwypePoint[] points;
	public SwypeData(File f) throws IOException {
		JsonParser parser = new JsonParser();
		JsonReader reader = new JsonReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(f))));
		JsonObject container = parser.parse(reader).getAsJsonObject();
		word = container.get("word").getAsString();
		JsonArray pointArray = container.get("data").getAsJsonArray();
		points = new SwypePoint[pointArray.size()];
		for (int i=0; i<pointArray.size(); i++) {
			JsonObject p = pointArray.get(i).getAsJsonObject();
			double x = p.get("x").getAsDouble();
			double y = p.get("y").getAsDouble();
			long time = p.get("time").getAsLong();
			points[i] = new SwypePoint(x, y, time);
		}
	}
}