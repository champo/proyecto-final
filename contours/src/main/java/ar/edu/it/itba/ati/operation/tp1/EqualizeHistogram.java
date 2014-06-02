package ar.edu.it.itba.ati.operation.tp1;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class EqualizeHistogram extends Operation {

	@Override
	public void operate(final Canvas original) {
		equalize(original.red, original);
		equalize(original.green, original);
		equalize(original.blue, original);
	}

	public void equalize(final double[] array, final Canvas original) {
		final int[] histData = getColorOccurrences(array);

		for (int i = 0; i < array.length; i++) {
			final int grayLevel = (int) array[i];

			double newValue = 0;
			for (int k = 0; k < grayLevel; k++) {
				newValue += histData[k];
			}

			newValue = newValue * (255.0 / array.length);
			array[i] = newValue;
		}
	}

	private int[] getColorOccurrences(final double[] array) {
		final int[] dataset = new int[256];

		for (final double element : array) {
			final int grayLevel = (int) element;
			dataset[grayLevel] += 1;
		}

		return dataset;
	}

	@Override
	public String toString() {
		return "Equalización de histograma";
	}
}
