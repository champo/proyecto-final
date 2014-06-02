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

public class IsotropicDifussion extends Operation {

	private int iterations;

	public IsotropicDifussion(final int iterations) {
		this.iterations = iterations;
	}

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < iterations; i++) {
			operateOnLayer(original.red, original.width, original.height);
			operateOnLayer(original.green, original.width, original.height);
			operateOnLayer(original.blue, original.width, original.height);
		}
	}

	private void operateOnLayer(final double[] layer, final int width, final int height) {
		final double newLayer[] = Arrays.copyOf(layer, layer.length);

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
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

				final double Cnij = 1;
				final double Csij = 1;
				final double Ceij = 1;
				final double Coij = 1;

				final double resultColor = oldValueIJ
						+ 0.25
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
	public Container getPanel() {
		final Container c = new JPanel(new GridLayout(0, 2));
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
