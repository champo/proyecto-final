package ar.edu.it.itba.ati.operation.tp2.difusion;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Arrays;

import javax.swing.JPanel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class AnisotropicDifussion extends Operation {

	private double sigma;
	private int iterations;
	private final EdgeOperator edgeOperator;

	public AnisotropicDifussion(final EdgeOperator edgeOperator, final int iterations, final double sigma) {
		this.iterations = iterations;
		this.edgeOperator = edgeOperator;
		this.sigma = sigma;
	}

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < iterations; i++) {
			operateOnLayer(original.red, original.width, original.height);
			operateOnLayer(original.green, original.width, original.height);
			operateOnLayer(original.blue, original.width, original.height);
		}

//		new LinearNormalization(false).operate(original);
	}

	private void operateOnLayer(final double[] layer, final int width, final int height) {
		final double newLayer[] = Arrays.copyOf(layer, layer.length);

		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {

				final double oldValueIJ = layer[j * width + i];

				double DnIij = oldValueIJ;
				double DsIij = oldValueIJ;
				double DeIij = oldValueIJ;
				double DoIij = oldValueIJ;

				if (i > 0) {
					DnIij = layer[j * width + i - 1];
				}
				if (i < width - 1) {
					DsIij = layer[j * width + i + 1];
				}
				if (j < height - 1) {
					DeIij = layer[(j + 1) * width + i];
				}
				if (j > 0) {
					DoIij = layer[(j - 1) * width + i];
				}

				DnIij -= oldValueIJ;
				DsIij -= oldValueIJ;
				DeIij -= oldValueIJ;
				DoIij -= oldValueIJ;

				final double Cnij = edgeOperator.getValue(DnIij, sigma);
				final double Csij = edgeOperator.getValue(DsIij, sigma);
				final double Ceij = edgeOperator.getValue(DeIij, sigma);
				final double Coij = edgeOperator.getValue(DoIij, sigma);

				final double resultColor = oldValueIJ
						+ 0.25 * 0.8
						* (DnIij * Cnij + DsIij * Csij + DeIij * Ceij + DoIij
								* Coij);
				newLayer[j * width + i] = resultColor;
			}
		}

		for (int i = 0; i < layer.length; i++) {
			layer[i] = newLayer[i];
		}
	}

	@Override
	public String toString() {
		return edgeOperator.toString() + ": sigma = " + sigma + ", iterations = " + iterations;
	}

	@Override
	public Container getPanel() {
		final Container c = new JPanel(new GridLayout(0, 2));
		final TextField textField1 = new TextField(Double.toString(sigma));
		textField1.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {
				try {
					sigma = Double.valueOf(textField1.getText());
					didChange();
				} catch (final NumberFormatException e ) {

				}
			}
		});
		c.add(textField1);
		final TextField textField = new TextField(Integer.toString(iterations));
		textField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {
				try {
					iterations = Integer.valueOf(textField.getText());
					didChange();
				} catch (final NumberFormatException e ) {

				}
			}
		});
		c.add(textField);
		return c;
	}
}
