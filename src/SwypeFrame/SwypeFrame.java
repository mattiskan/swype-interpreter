package SwypeFrame;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.*;
import javax.swing.*;
public class SwypeFrame extends JFrame {
	BufferedImage keyBoard;
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
		getContentPane().add(new DrawArea());

	}

	private class DrawArea extends JComponent {
		public DrawArea() {
			
		}
		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g.drawImage(keyBoard, 0, 0, null);
		}
	
	}
	
}


