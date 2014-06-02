package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Synthethizer;


public class PrewittDetector extends AbstractDetector {

	public PrewittDetector(final Synthethizer synth) {
		super(synth);
	}

	@Override
	protected BaseMask[] buildMasks(final int width, final int height) {

		Mask downMask = new Mask(width, height, new double[][] { { -1, -1, -1 }, { 0, 0, 0 }, { 1, 1, 1 } });
		Mask leftMask = new Mask(width, height, new double[][] { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } });
		return new BaseMask[] { downMask, leftMask };
	}

}
