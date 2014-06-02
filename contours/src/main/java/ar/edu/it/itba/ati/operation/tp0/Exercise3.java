package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Exercise3 extends Operation{

	@Override
	public void operate(Canvas original) {
		new DrawCircle(original.getWidth() / 2, original.getHeight() / 2, original.getWidth() / 4, 255, 255, 255).operate(original);
	}

}
