package ar.edu.it.itba.ati.operation.tp3;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.Synthethizer;
import ar.edu.it.itba.ati.operation.tp1.ToGray;
import ar.edu.it.itba.ati.operation.tp2.edge.SobelDetector;

public class NotMaximumSupression extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);

		final int[] moveX = new int[original.size];
		final int[] moveY = new int[original.size];

		new SobelDetector(new Synthethizer() {

			@Override
			public void synth(final Canvas original, final Canvas... materials) {

				for (int i = 0; i < original.red.length; i++) {
					original.red[i] = Math.max(materials[0].red[i], materials[1].red[i]);

					if (materials[0].red[i] == 0) {
						moveX[i] = 0;
						moveY[i] = 1;
					} else {
						double angle = Math.atan(materials[1].red[i] / materials[0].red[i]) * 180 / Math.PI;
						if (angle < -67.5 || angle > 65.7) {
							moveX[i] = 0;
							moveY[i] = 1;
						} else if (-67.5 <= angle && angle < -22.5) {
							moveX[i] = 1;
							moveY[i] = -1;
						} else if (-22.5 <= angle && angle < 22.5) {
							moveX[i] = 1;
							moveY[i] = 0;
						} else {
							moveX[i] = 1;
							moveY[i] = 1;
						}
					}


				}
			}
		}).operate(original);

		double[] newValue = new double[original.size];
		for (int i = 0; i < original.red.length; i++) {

			double before;
			double after;

			int beforeIndex = i - moveX[i] - moveY[i] * original.width;
			if (beforeIndex < 0) {
				before = Double.MIN_VALUE;
			} else {
				before = original.red[beforeIndex];
			}

			int afterIndex = i + moveX[i] + moveY[i] * original.width;
			if (afterIndex >= original.size) {
				after = Double.MIN_VALUE;
			} else {
				after = original.red[afterIndex];
			}

			if (Math.max(before, after) > original.red[i]) {
				newValue[i] = 0;
			} else {
				newValue[i] = original.red[i];
			}
		}

		for (int i = 0; i < original.size; i++) {
			original.red[i] = newValue[i];
			original.green[i] = newValue[i];
			original.blue[i] = newValue[i];
		}

	}

}
