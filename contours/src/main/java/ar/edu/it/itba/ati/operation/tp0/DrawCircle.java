package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

/**
 * Show a white circle in the middle of a 200x200 image
 * 
 * @author eordano
 */
public class DrawCircle extends Operation {

	private int r;
	private int x;
	private int y;
	private int red;
	private int green;
	private int blue;

	public DrawCircle(int x, int y, int r, int red, int green, int blue) {
		this.r = r;
		this.x = x;
		this.y = y;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public String toString() {
		return "Circulo de radio " + r + " en (" + x + "," + y + ") de color " + red + "," + green + "," + blue;
	}

	@Override
	public void operate(Canvas original) {
		for (int i = x - r; i <= x + r; i++) {
			for (int j = y - r; j <= y + r; j++) {
                double xdist = Math.pow(i - x, 2);
                double ydist = Math.pow(j - y, 2);
                double distance = Math.sqrt(xdist + ydist);
                if (distance < r) {
                	original.setPixel(i, j, red, green, blue);
                }
			}
		}
	}
}
