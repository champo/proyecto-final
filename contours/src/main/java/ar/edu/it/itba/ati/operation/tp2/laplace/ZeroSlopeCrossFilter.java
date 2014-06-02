package ar.edu.it.itba.ati.operation.tp2.laplace;

import java.util.Arrays;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class ZeroSlopeCrossFilter extends Operation {

	private final double umbral;

	public ZeroSlopeCrossFilter(final double umbral) {
		this.umbral = umbral;
	}

	@Override
	public void operate(final Canvas original) {
		operateOnLayer(original.red, original.getWidth(), original.getHeight());
		operateOnLayer(original.green, original.getWidth(), original.getHeight());
		operateOnLayer(original.blue, original.getWidth(), original.getHeight());
	}

	private void operateOnLayer(final double[] layer, final int width, final int height) {

		double[] duplicate = Arrays.copyOf(layer, layer.length);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				double max = Double.MIN_VALUE;
				double min = Double.MAX_VALUE;

				for (int i = -1; i <= 1; i++) {
					for (int j = -1; j <= 1; j++) {

						int index = x + i + (y + j) * width;
						if (!(i == 0 && j == 0) && index >= 0 && index < layer.length) {
							max = Math.max(max, duplicate[index]);
							min = Math.min(min, duplicate[index]);
						}
					}
				}
				double diff = Math.abs(max + min);
				if (min < 0 && max > 0 && diff > umbral) {
					layer[y * width + x] = 1;
				} else {
					layer[y * width + x] = 254;

				}

			}
		}

		layer[0] = 0;
	}
}
