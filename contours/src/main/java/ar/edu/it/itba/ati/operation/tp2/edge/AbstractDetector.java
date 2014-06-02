package ar.edu.it.itba.ati.operation.tp2.edge;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.Synthethizer;

public abstract class AbstractDetector extends Operation {

	protected final Synthethizer synth;

	public AbstractDetector(final Synthethizer synth) {
		this.synth = synth;
	}

	protected static class Mask extends BaseMask {

		private final double[][] mask;

		public Mask(final int width, final int height, final double[][] mask) {
			super(0, 0, width, height, 1);
			this.mask = mask;
		}

		@Override
		protected double[][] buildMask() {
			return mask;
		}

		@Override
		protected double normalize(final double value, final double max_value,
				final double min_value) {
			return value;
		}

		@Override
		public String toString() {
			return mask.toString();
		}
	}

	@Override
	public void operate(final Canvas original) {


		BaseMask[] masks = buildMasks(original.width, original.height);

		Canvas[] canvases = new Canvas[masks.length];
		try {
			for (int i = 0; i < canvases.length; i++) {
				canvases[i] = original.clone();
			}
		} catch (CloneNotSupportedException e) {
			return;
		}

		for (int i = 0; i < canvases.length; i++) {
			masks[i].operate(canvases[i]);
		}

		synth.synth(original, canvases);
	}

	abstract protected BaseMask[] buildMasks(final int width, final int height);

}
