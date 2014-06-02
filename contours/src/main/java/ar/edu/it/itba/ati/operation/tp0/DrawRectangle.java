package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

/**
 * Show a white circle in the middle of a 200x200 image
 * 
 * @author eordano
 */
public class DrawRectangle extends Operation {
	
	private int x1;
	private int y1;
	private int x2;
	private int y2;
	private int red;
	private int green;
	private int blue;

	public DrawRectangle(int x1, int y1, int x2, int y2, int red, int green, int blue) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	@Override
	public String toString() {
		return "Cuadrado de lado 60";
	}

	@Override
	public void operate(Canvas original) {
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				original.setPixel(i, j, red, green, blue);
			}
		}
	}
}
