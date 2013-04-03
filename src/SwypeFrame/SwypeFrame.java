package SwypeFrame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.imageio.*;
import javax.swing.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
public class SwypeFrame extends JFrame {
	BufferedImage keyBoard;
	String word = "";
	public static void main(String[] args) {
		new SwypeFrame();
	}
	
	public SwypeFrame() {
		super("Swype!");
		setSize(800, 480);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		try {
			keyBoard = ImageIO.read(new File("files/keyboard.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//getContentPane().add(new DrawArea());

		setVisible(true);
		setResizable(false);
	}

	private class DrawArea extends JComponent {
		Point2D[] points;
		public DrawArea(Point2D[] points) {
			this.points = points;
		}
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(keyBoard, 0, 0, null);
			g2.setColor(Color.WHITE);
			g2.drawString(word, 0, 0);
		}
		
		
	}
		
}


