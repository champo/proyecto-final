package ar.edu.it.itba.ati.operation.tp2.syntethizer;

public class Max extends AbstractSynthethizer {

	@Override
	protected double synthValue(final double[] value) {
		double res = Double.MIN_NORMAL;
		for (double d : value) {
			res = Math.max(res, d);
		}
		return res;
	}

}
