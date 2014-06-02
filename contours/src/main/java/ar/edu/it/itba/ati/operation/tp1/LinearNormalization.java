package ar.edu.it.itba.ati.operation.tp1;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class LinearNormalization extends Operation {

	private final boolean strict;

	public LinearNormalization() {
		this(true);
	}

	public LinearNormalization(final boolean strict) {
		this.strict = strict;
	}

	@Override
	public void operate(final Canvas original) {

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		final int size = original.size;
		for (int i = 0; i < size; i++) {
			min = Math.min(min, original.red[i]);
			max = Math.max(max, original.red[i]);
			min = Math.min(min, original.green[i]);
			max = Math.max(max, original.green[i]);
			min = Math.min(min, original.blue[i]);
			max = Math.max(max, original.blue[i]);
		}

		double segmentLength = 255;
		if (!strict) {

			min = Math.min(0, min);
			max = Math.max(255, max);
		}

		final double ratio = segmentLength / (max - min);

		for (int i = 0; i < size; i++) {
			original.red[i] = (original.red[i] - min) * ratio;
			original.green[i] = (original.green[i] - min) * ratio;
			original.blue[i] = (original.blue[i] - min) * ratio;
		}
	}

	@Override
	public String toString() {
		return "Normalización lineal";
	}

}
