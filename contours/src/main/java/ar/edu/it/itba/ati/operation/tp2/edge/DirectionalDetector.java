package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Synthethizer;

public abstract class DirectionalDetector extends AbstractDetector {

	protected final boolean simple;

	public DirectionalDetector(final Synthethizer synth, final boolean simple) {
		super(synth);
		this.simple = simple;
	}

	@Override
	protected BaseMask[] buildMasks(final int width, final int height) {

		if (simple) {

			return new BaseMask[] {
				buildSimpleMask(width, height)
			};
		} else {
			return buildAllMasks(width, height);
		}

	}

	protected abstract Mask buildSimpleMask(int width, int height);

	protected abstract BaseMask[] buildAllMasks(int width, int height);
}
