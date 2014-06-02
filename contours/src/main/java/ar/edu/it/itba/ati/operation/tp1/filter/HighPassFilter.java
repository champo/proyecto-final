package ar.edu.it.itba.ati.operation.tp1.filter;

import ar.edu.it.itba.ati.operation.BaseMask;


public class HighPassFilter extends BaseMask {

	public HighPassFilter(final int x, final int y, final int width, final int height, final int radius) {
		super(x, y, width, height, radius);
	}

	@Override
	protected double[][] buildMask() {

		int sideLength = 2 * radius + 1;
		double[][] mask = new double[sideLength][sideLength];
		for (int i = 0; i < sideLength; i++) {
			for (int j = 0; j < sideLength; j++) {
				mask[i][j] = -1;
			}
		}

		mask[radius][radius] = sideLength * sideLength - 1;

		return mask;
	}
}
