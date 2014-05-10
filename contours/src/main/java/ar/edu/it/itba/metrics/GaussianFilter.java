package ar.edu.it.itba.metrics;


public class GaussianFilter {

	private double[][] mask;
	private final int radius;
	private final double sigma;

	public GaussianFilter(final double sigma, final int radius) {

		this.sigma = sigma;
		this.radius = radius;

		calculateMask(sigma, radius);
	}

	private void calculateMask(final double sigma, final int radius) {
		int sideLength = 2  * radius + 1;

		this.mask = new double[sideLength][sideLength];
		double sigmaSquared = Math.pow(sigma, 2);

		for (int i = -radius; i < radius + 1; i++) {
			for (int j = -radius; j < radius + 1; j++) {
				mask[i + radius][j + radius] = Math.exp(- (double)(i * i + j * j) / (2 * sigmaSquared)) / (2 * Math.PI * sigmaSquared);
			}
		}
	}

	public float[][] operate(final float[][] array) {

		final float[][] newArray = array.clone();

		final int width = array.length;
		final int height = array[0].length;

		for (int win_x = 0; win_x < width; win_x++) {
			for (int win_y = 0; win_y < height; win_y++) {

				float accum = 0;

				for (int mask_x = 0; mask_x < mask.length; mask_x++) {
					for (int mask_y = 0; mask_y < mask.length; mask_y++) {

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

						accum += mask[mask_x][mask_y] * array[delta_x][delta_y];
					}
				}
				newArray[win_x][win_y] = accum;
			}
		}

		return newArray;
	}

}
