package ar.edu.it.itba.ati.operation.tp1.filter;

import java.util.Arrays;

import ar.edu.it.itba.ati.operation.BaseMask;


public class MedianFilter extends BaseMask {

	public MedianFilter(final int x, final int y, final int width, final int height, final int radius) {
		super(x, y, width, height, radius);
	}

	@Override
	protected double[][] buildMask() {
		return null;
	}

	@Override
	protected void operateOnLayer(final double[] array, final int width, final int height) {
		final double[] newArray = new double[array.length];

		int sideLength = 2 * radius + 1;
		double min_value = Double.POSITIVE_INFINITY;
		double max_value = Double.NEGATIVE_INFINITY;
		for (int win_x = x; win_x < x + this.width; win_x++) {
			for (int win_y = y; win_y < y + this.height; win_y++) {

				final double[] values = new double[sideLength * sideLength];
				final int win_pos = win_x + win_y * width;
				int l = 0;

				for (int mask_x = 0; mask_x < sideLength; mask_x++) {
					for (int mask_y = 0; mask_y < sideLength; mask_y++) {

						int delta_x = win_x + mask_x - radius;
						int delta_y = win_y + mask_y - radius;

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

						values[l++] = array[delta_pos];
					}
				}
				Arrays.sort(values);
				final double median = values[l/2];
				newArray[win_pos] = median;
				if (median < min_value) {
					min_value = median;
				}
				if (median > max_value) {
					max_value = median;
				}
			}
		}
		final double scale = 255 / (max_value - min_value);

		for (int win_x = x; win_x < x + this.width; win_x++) {
			for (int win_y = y; win_y < y + this.height; win_y++) {

				final int win_pos = win_x + win_y * width;
				array[win_pos] = (newArray[win_pos] - min_value) * scale;
			}
		}
	}
}
