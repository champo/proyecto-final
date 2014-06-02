package ar.edu.it.itba.ati.operation.tp2.syntethizer;


public class Average extends AbstractSynthethizer {

	@Override
	protected double synthValue(final double[] value) {
		double val = 0;
		for (double d : value) {
			val += d;
		}

		return val / value.length;
	}

}
