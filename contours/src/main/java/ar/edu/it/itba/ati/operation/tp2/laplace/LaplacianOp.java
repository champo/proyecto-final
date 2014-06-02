package ar.edu.it.itba.ati.operation.tp2.laplace;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.BaseMask;

public class LaplacianOp extends BaseMask {

	public LaplacianOp(final int x, final int y, final int width, final int height) {
		super(x, y, width, height, 1);
	}

	@Override
	protected double[][] buildMask() {
		return new double[][] { {0, -1, 0}, {-1, 4, -1}, {0, -1, 0} };
	}

	@Override
	public void operate(final Canvas original) {
		super.operate(original);
		new ZeroCrossFilter().operate(original);
	}

	@Override
	protected double normalize(final double value, final double max_value, final double min_value) {
		return value;
	}
}
