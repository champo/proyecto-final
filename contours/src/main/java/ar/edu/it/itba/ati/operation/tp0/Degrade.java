package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

/**
 * Show a degrade
 * 
 * @author eordano
 */
public class Degrade extends Operation {

	int r1;
	int r2;
	int g1;
	int g2;
	int b1;
	int b2;
	boolean horizontal;
	
	public Degrade(int r1, int g1, int b1, int r2, int g2, int b2, boolean horizontal) {
		this.r1 = r1;
		this.r2 = r2;
		this.g1 = g1;
		this.g2 = g2;
		this.b1 = b1;
		this.b2 = b2;
		this.horizontal = horizontal;
	}

	@Override
	public String toString() {
		return horizontal ? "Degrade horizontal" : "Degrade vertical";
	}

	@Override
	public void operate(Canvas original) {
		int width = horizontal ? original.getWidth() : original.getHeight();
		int height = horizontal ? original.getHeight() : original.getWidth();
		for (int i = 0; i < width; i++) {
			int r = r1 + (r2 - r1) * i / width;
			int g = g1 + (g2 - g1) * i / width;
			int b = b1 + (b2 - b1) * i / width;
			for (int j = 0; j < height; j++) {
				if (horizontal) {
					original.setPixel(i, j, r, g, b);
				} else {
					original.setPixel(j, i, r, g, b);
				}
			}
		}
	}
}
