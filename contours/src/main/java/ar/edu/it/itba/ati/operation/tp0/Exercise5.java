package ar.edu.it.itba.ati.operation.tp0;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Exercise5 extends Operation {

	@Override
	public void operate(Canvas original) {
		new Degrade(255, 0, 0, 0, 255, 0, true).operate(original);
	}
}
