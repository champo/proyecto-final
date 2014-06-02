package ar.edu.it.itba.ati.operation.tp2.laplace;

import java.awt.Container;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JLabel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.BaseMask;
import ar.edu.it.itba.ati.operation.tp1.ToGray;

public class MarrHildreth extends BaseMask {

	private double sigma;
	private double umbral;

	public MarrHildreth(final int x, final int y, final int width, final int height, final int radius, final double sigma, final double umbral) {
		super(x, y, width, height, radius);
		this.sigma = sigma;
		this.umbral = umbral;
	}

	@Override
	protected double[][] buildMask() {

		final int sideLength = 2  * radius + 1;
		final double[][] mask = new double[sideLength][sideLength];
		final double sigmaSquared = Math.pow(sigma, 2);
		final double sigmaCubed = Math.pow(sigma, 3);

		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {

				final double iSquared = Math.pow(i, 2);
				final double jSquared = Math.pow(j, 2);

				final double factor = Math.pow(Math.sqrt(2 * Math.PI) * sigmaCubed, -1);
				final double exp = - (iSquared + jSquared) / (2 * sigmaSquared);
				final double term = 2 - (iSquared + jSquared)/sigmaSquared;
				final double pixelValue = -1 * factor * term * Math.pow(Math.E, exp);
				mask[i + radius][j + radius] = pixelValue;
			}
		}

		return mask;
	}

	@Override
	protected double normalize(final double value, final double max_value,
			final double min_value) {
		return value;
	}

	@Override
	public void operate(final Canvas original) {
		new ToGray().operate(original);
		super.operate(original);
		new ZeroSlopeCrossFilter(umbral).operate(original);
	}

	@Override
	public Container getPanel() {
		final Container container = super.getPanel();

		container.add(new JLabel("Sigma"));
		final TextField sigmaField = new TextField(Double.toString(sigma));
		container.add(sigmaField);

		sigmaField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					sigma = Double.valueOf(sigmaField.getText());
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		container.add(new JLabel("Threshold for the slope"));
		final TextField zeroField = new TextField(Double.toString(umbral));
		container.add(zeroField);

		zeroField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					umbral = Double.valueOf(zeroField.getText());
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		return container;
	}
}
