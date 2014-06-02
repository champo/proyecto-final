package ar.edu.it.itba.ati.operation.tp2.difusion;


public class LorentzOperator implements EdgeOperator {

	@Override
	public double getValue(final double x, final double sigma) {
		final double den = Math.pow(Math.abs(x) / sigma, 2) + 1;
		return 1/den;
	}

	@Override
	public String toString() {
		return "Lorentz Aniso";
	}
}
