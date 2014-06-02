package ar.edu.it.itba.ati.operation.tp2.syntethizer;

public class Modulo extends AbstractSynthethizer {

	@Override
	protected double synthValue(final double[] value) {

		double res = 0;

		for (int i = 0; i < value.length; i++) {
			res += Math.pow(value[i], 2);
		}

		return Math.pow(res, 1.0 / value.length);
	}

}
