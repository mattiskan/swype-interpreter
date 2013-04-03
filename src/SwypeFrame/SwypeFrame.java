package SwypeFrame;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import javax.imageio.*;
import javax.swing.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
public class SwypeFrame extends JFrame {
	BufferedImage keyBoard;
	SwypeData data;
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
		try {
			data = new SwypeData(new File("../files/axel_0.json"));
			getContentPane().add(new DrawArea());
		} catch (IOException e) {
			e.printStackTrace();
		}
		setVisible(true);
		setResizable(false);
	}

	private class DrawArea extends JComponent {
		public DrawArea() {
		}
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g2.drawImage(keyBoard, 0, 0, null);
			g2.setColor(Color.WHITE);
			g2.drawString(data.getWord(), 0, 0);
		}
		
		
	}
		
}


