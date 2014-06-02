package ar.edu.it.itba.ati.operation.tp2.threshold;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.Threshold;
import ar.edu.it.itba.ati.operation.tp1.ToGray;

public class Otsu extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);

		int t = 0;
		double maxVariance = 0;

		double[] distribution = greyDistribution(original.red);

		for (int i = 0; i < 255; i++) {

			double w1 = 0;
			double w2 = 0;

			double u1 = 0;
			double u2 = 0;

			for (int j = 0; j < i + 1; j++) {
				w1 += distribution[j];
				u1 += j * distribution[j];
			}

			for (int j = i + 1; j < 255; j++) {
				w2 += distribution[j];
				u2 += j * distribution[j];
			}

			if (w1 == 0 || w2 == 0) {
				continue;
			}

			double uT = u2 + u1;

			u1 /= w1;
			u2 /= w2;
			double variance = w1 *  Math.pow(u1 - uT, 2) + w2 * Math.pow(u2 - uT, 2);

			if (variance > maxVariance) {
				maxVariance = variance;
				t = i;
			}
		}

		System.out.println(t);
		new Threshold(t).operate(original);
	}

	private double[] greyDistribution(final double[] array) {
		final double[] dataset = new double[256];

		for (final double element : array) {
			final int grayLevel = (int) element;
			dataset[grayLevel] += 1;
		}

		for (int i = 0; i < 256; i++) {
			dataset[i] /= array.length;
		}

		return dataset;
	}
}
