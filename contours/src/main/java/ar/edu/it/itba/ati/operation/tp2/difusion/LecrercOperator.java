package ar.edu.it.itba.ati.operation.tp2.difusion;


public class LecrercOperator implements EdgeOperator {

	@Override
	public double getValue(final double x, final double sigma) {
		return Math.exp( -Math.pow(Math.abs(x) / sigma, 2));
	}

	@Override
	public String toString() {
		return "Lecrerc Aniso";
	}
}
