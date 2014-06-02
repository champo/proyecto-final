package ar.edu.it.itba.ati.operation.tp2.filter;

import java.awt.Container;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JLabel;

import ar.edu.it.itba.ati.operation.BaseMask;

public class GaussianFilter extends BaseMask {

	private double sigma;

	public GaussianFilter(final int x, final int y, final int width, final int height, final int radius, final double sigma) {
		super(x, y, width, height, radius);
		this.sigma = sigma;
	}

	@Override
	protected double[][] buildMask() {

		int sideLength = 2  * radius + 1;
		double[][] mask = new double[sideLength][sideLength];
		double sigmaSquared = Math.pow(sigma, 2);

		for (int i = -radius; i < radius + 1; i++) {
			for (int j = -radius; j < radius + 1; j++) {
				mask[i + radius][j + radius] = Math.pow(Math.E, - (double)(i * i + j * j) / (2 * sigmaSquared)) / (2.0 * Math.PI * sigmaSquared);
			}
		}

		return mask;
	}

	@Override
	public Container getPanel() {
		Container container = super.getPanel();

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

		return container;
	}
}
