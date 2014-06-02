package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Synthethizer;

public class Kirsh extends DirectionalDetector {

	public Kirsh(final Synthethizer synth, final boolean simple) {
		super(synth, simple);
	}

	@Override
	protected Mask buildSimpleMask(final int width, final int height) {
		return new Mask(width, height, new double[][] { { 5, 5, 5 }, { -3, 0, -3 }, { -3, -3, -3 } });
	}

	@Override
	protected BaseMask[] buildAllMasks(final int width, final int height) {
		return new BaseMask[] {
				/*
				 *  5  5  5
				 * -3  0 -3
				 * -3 -3 -3
				 */
			new Mask(width, height, new double[][] { { 5, 5, 5 }, { -3, 0, -3 }, { -3, -3, -3 } }),
			new Mask(width, height, new double[][] { { 0, 5, -3 }, { 5, 0, -3 }, { -3, -3, 0 } }),
			new Mask(width, height, new double[][] { { 5, -3, -3 }, { 5, 0, -3 }, { 5, -3, -3 } }),
			new Mask(width, height, new double[][] { { -3, -3, 0 }, { 5, 0, -3 }, { 0, 5, -3 } }),
		};
	}
}
