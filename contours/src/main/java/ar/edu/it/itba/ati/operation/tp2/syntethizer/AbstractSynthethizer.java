package ar.edu.it.itba.ati.operation.tp2.syntethizer;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Synthethizer;
import ar.edu.it.itba.ati.operation.tp1.LinearNormalization;

public abstract class AbstractSynthethizer implements Synthethizer {

	@Override
	public void synth(final Canvas original, final Canvas... materials) {

		for (int i = 0; i < original.red.length; i++) {
			original.red[i] = synthValue(extractRed(materials, i));
			original.green[i] = synthValue(extractGreen(materials, i));
			original.blue[i] = synthValue(extractBlue(materials, i));
		}

		new LinearNormalization(false).operate(original);
	}

	private double[] extractRed(final Canvas[] materials, final int pos) {
		double[] res = new double[materials.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = materials[i].red[pos];
		}
		return res;
	}

	private double[] extractGreen(final Canvas[] materials, final int pos) {
		double[] res = new double[materials.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = materials[i].green[pos];
		}
		return res;
	}


	private double[] extractBlue(final Canvas[] materials, final int pos) {
		double[] res = new double[materials.length];
		for (int i = 0; i < res.length; i++) {
			res[i] = materials[i].blue[pos];
		}
		return res;
	}

	abstract protected double synthValue(double[] value);

}
