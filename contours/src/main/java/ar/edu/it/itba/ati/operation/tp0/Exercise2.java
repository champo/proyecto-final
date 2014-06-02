package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Exercise2 extends Operation {

	@Override
	public void operate(Canvas original) {
		new DrawRectangle(original.getWidth() /2 - 40,
						  original.getHeight() /2 - 40,
						  original.getWidth() /2 + 40,
						  original.getHeight() /2 + 40,
						  255, 255, 255).operate(original);
	}
}
