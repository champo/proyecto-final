package ar.edu.it.itba.ati.operation.tp1;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class ToGray extends Operation {

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < original.size; i++) {
			final double value = (original.red[i] + original.green[i] + original.blue[i]) / 3;
			original.red[i] = value;
			original.green[i] = value;
			original.blue[i] = value;
		}
	}
	@Override
	public String toString() {
		return "Conversión a grises";
	}
}
