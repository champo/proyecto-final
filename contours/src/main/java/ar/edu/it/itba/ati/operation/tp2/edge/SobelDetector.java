package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Synthethizer;

public class SobelDetector extends AbstractDetector {

	public SobelDetector(final Synthethizer synth) {
		super(new Synthethizer() {

			@Override
			public void synth(final Canvas original, final Canvas... materials) {
				for (int i = 0; i < original.red.length; i++) {
					double val = Math.max(materials[0].red[i], materials[1].red[i]);
					original.green[i] = val;
					original.red[i] = val;
					original.blue[i] = val;
				}
				for (int i = 0; i < original.red.length; i++) {
				}
				for (int i = 0; i < original.red.length; i++) {
				}
			}
		});
	}

	@Override
	protected BaseMask[] buildMasks(final int width, final int height) {

		return new BaseMask[] {
			new Mask(width, height, new double[][] { { -1, 0 , 1 }, { -2, 0, 2 }, { -1, 0, 1 } }),
			new Mask(width, height, new double[][] { { -1, -2 , -1 }, { 0, 0, 0 }, { 1, 2, 1 } }),
		};
	}



}
