package ar.edu.it.itba.ati.operation.tp1;


import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class MultiplyScalar extends Operation {

	double c;
	public MultiplyScalar(final double i) {
		c = i;
	}

	@Override
	public String toString() {
		return "Multiplicar por escalar: " + c;
	}

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < original.getWidth() * original.getHeight(); i++) {
			original.red[i] *= c;
			original.green[i] *= c;
			original.blue[i] *= c;
		}

		new LinearNormalization(false).operate(original);
	}


	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

		panel.add(new JLabel("Escalar: "));
		final TextField redField = new TextField(Double.toString(c));
		panel.add(redField);

		redField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					final double newC = Double.valueOf(redField.getText());
					c = newC;
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});

		return panel;
	}
}
