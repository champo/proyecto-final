package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Synthethizer;

public class Mask4 extends DirectionalDetector {

	public Mask4(final Synthethizer synth, final boolean simple) {
		super(synth, simple);
	}

	@Override
	protected Mask buildSimpleMask(final int width, final int height) {
		return new Mask(width, height, new double[][] { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } });
	}

	@Override
	protected BaseMask[] buildAllMasks(final int width, final int height) {
		return new BaseMask[] {
				/*
				 *  1  2  1
				 *  0  0  0
				 * -1 -2 -1
				 */
			new Mask(width, height, new double[][] { { 1, 2, 1 }, { 0, 0, 0 }, { -1, -2, -1 } }),
			new Mask(width, height, new double[][] { { 0, 1, 0 }, { 1, 0, -1 }, { 0, -1, 0 } }),
			new Mask(width, height, new double[][] { { 1, 0, -1 }, { 2, 0, -2 }, { 1, 0, -1 } }),
			new Mask(width, height, new double[][] { { 0, -1, 0 }, { 1, 0, -1 }, { 0, 1, 0 } }),
		};
	}

}
