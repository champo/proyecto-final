package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class ChangeOnePixel extends Operation {

	final int x, y, red, green, blue;

	public ChangeOnePixel(final int x, final int y, final int red, final int green, final int blue) {
		this.x = x;
		this.y = y;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	@Override
	public void operate(final Canvas original) {
		original.setPixel(x, y, red, green, blue);
	}

}
