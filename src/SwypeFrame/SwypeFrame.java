package SwypeFrame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
public class SwypeFrame extends JFrame {
	BufferedImage keyBoard;
	SwypeData data;
	List<Point2D> mark = new ArrayList<Point2D>();
	List<Character> markChar = new ArrayList<Character>();
	public static void main(String[] args) {
		SwypeFrame sf = new SwypeFrame(new File("files/slow_0.json"));
		sf.markPoint(new Point2D.Double(5.0, 5.0), 'a');
		sf.setVisible(true);
	}
	public SwypeFrame(File json) {
		super(json.getName());
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			keyBoard = ImageIO.read(new File("files/keyboard.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		getContentPane().add(new DrawArea());
		loadJsonFile(json);
		pack();
		setResizable(false);
	}
	
	public void loadJsonFile(File json) {
		data = new SwypeData(json);
	}
	
	private int defaultChar = 1;
	public void markPoint(Point2D point) {
		markPoint(point, Character.forDigit(defaultChar++, 10));
	}

	public void markPoint(Point2D point, char c) {
		mark.add(point);
		markChar.add(c);
		repaint();
	}
	
	private class DrawArea extends JComponent {
		public DrawArea() {
			setPreferredSize(new Dimension(800, 480));
			setMinimumSize(new Dimension(800, 480));
		}
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;

			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, 800, 38);
			g2.drawImage(keyBoard, 0, 38, null);
			g2.setColor(Color.WHITE);
			g2.drawString(data.getWord(), 30, 50);
			SwypePoint last = null;
			g2.setColor(Color.RED);
			g2.setStroke(new BasicStroke(3));
			for (SwypePoint p : data) {
				if (last!=null)
					g2.drawLine((int)last.x, (int)last.y, (int)p.x, (int)p.y);
				last = p;
				//g2.fillRect((int)p.x, (int)p.y, 2, 2);
			}
			for (int i=0; i<mark.size(); i++) {
				Point2D p = mark.get(i);
				Character c = markChar.get(i);
				g2.setColor(Color.GREEN);
				g2.fillOval((int)p.getX()-7, (int)p.getY()-7, 14, 14);
				
				if (c!=null) {
					g2.setColor(Color.BLACK);
					g2.drawString(c.toString(), (int)p.getX()-2, (int)p.getY()+4);
				}
			}
		}
		
		
	}
		
}


