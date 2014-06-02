package ar.edu.it.itba.ati.operation.tp2.laplace;

import java.awt.Container;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JLabel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.BaseMask;

public class LaplacianOpLocal extends BaseMask {

	private double umbral;

	public LaplacianOpLocal(final int x, final int y, final int width, final int height, final double umbral) {
		super(x, y, width, height, 1);
		this.umbral = umbral;
	}

	@Override
	protected double[][] buildMask() {
		return new double[][] { {0, -1, 0}, {-1, 4, -1}, {0, -1, 0} };
	}

	@Override
	protected double normalize(final double value, final double max_value,
			final double min_value) {
		return value;
	}

	@Override
	public void operate(final Canvas original) {
		super.operate(original);
		new ZeroSlopeCrossFilter(umbral).operate(original);
	}

	@Override
	public Container getPanel() {

		final Container container = super.getPanel();
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
