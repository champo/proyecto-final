package ar.edu.it.itba.ati.operation.tp1;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class Threshold extends Operation {

	double c;

	public Threshold(final double c) {
		this.c = c;
	}

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < original.size; i++) {
			if ((original.red[i] + original.green[i] + original.blue[i]) / 3 < c) {
				original.red[i] = 0;
				original.green[i] = 0;
				original.blue[i] = 0;
			} else {
				original.red[i] = 255;
				original.green[i] = 255;
				original.blue[i] = 255;
			}
		}
	}

	@Override
	public String toString() {
		return "Umbral - " + c;
	};

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

		panel.add(new JLabel("Umbral:"));
		final JSlider uf = new JSlider(0, 255, (int) c);
		panel.add(uf);

		uf.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newC = uf.getValue();
				c = newC;
				didChange();
			}
		});

		return panel;
	}
}
