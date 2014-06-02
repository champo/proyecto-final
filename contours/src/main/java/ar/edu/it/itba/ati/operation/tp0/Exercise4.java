package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Exercise4 extends Operation {

	@Override
	public void operate(Canvas original) {
		new Degrade(0, 0, 0, 255, 255, 255, true).operate(original);
	}
}
