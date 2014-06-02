package ar.edu.it.itba.ati.operation.tp1;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Negative extends Operation {

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < original.size; i++) {
			original.red[i] = 255 - original.red[i];
			original.green[i] = 255 - original.green[i];
			original.blue[i] = 255 - original.blue[i];
		}
	}
	@Override
	public String toString() {
		return "Negativo";
	}
}
