package ar.edu.it.itba.ati.operation.tp2.edge;

import java.util.Arrays;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp2.syntethizer.Modulo;

public class RobertsDetector extends Operation {

	private double[] operateOnLayer(final double[] array, final double[][] mask, final int width, final int height) {

		final double[] newArray = Arrays.copyOf(array, array.length);

		double min_value = Double.POSITIVE_INFINITY;
		double max_value = Double.NEGATIVE_INFINITY;
		for (int win_x = 0; win_x < width; win_x++) {
			for (int win_y = 0; win_y < height; win_y++) {

				final int win_pos = win_x + win_y * width;
				double accum = 0;

				for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
					for (int mask_y = 0; mask_y < mask.length; mask_y++) {

						int delta_x = win_x + mask_x;
						int delta_y = win_y + mask_y;

						if (delta_x < 0) {
							delta_x = 0;
						}
						if (delta_x >= width) {
							delta_x = width - 1;
						}
						if (delta_y < 0) {
							delta_y = 0;
						}
						if (delta_y >= height) {
							delta_y = height - 1;
						}

						final int delta_pos = delta_x + delta_y * width;

						accum += mask[mask_y][mask_x] * array[delta_pos];
					}
				}
				newArray[win_pos] = accum;
				if (accum < min_value) {
					min_value = accum;
				}
				if (accum > max_value) {
					max_value = accum;
				}
			}
		}

		return newArray;
	}

	@Override
	public void operate(final Canvas original) {

		double[][] leftMask = { { 1, 0 }, { 0, -1 } };
		double[][] downMask = { { 0, 1 }, { -1, 0 } };

		// Assume B & W
		double[] leftRed = operateOnLayer(original.red, leftMask, original.width, original.height);
		double[] leftGreen = operateOnLayer(original.green, leftMask, original.width, original.height);
		double[] leftBlue = operateOnLayer(original.blue, leftMask, original.width, original.height);

		double[] downRed = operateOnLayer(original.red, downMask, original.width, original.height);
		double[] downGreen = operateOnLayer(original.green, downMask, original.width, original.height);
		double[] downBlue = operateOnLayer(original.blue, downMask, original.width, original.height);

		Canvas down = new Canvas(original.height, original.width, downRed, downGreen, downBlue);
		Canvas left = new Canvas(original.height, original.width, leftRed, leftGreen, leftBlue);
		new Modulo().synth(original, down, left);
	}

}
