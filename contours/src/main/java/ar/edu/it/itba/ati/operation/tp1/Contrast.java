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

public class Contrast extends Operation {

	private double x1;
	private double x2;
	private double y1;
	private double y2;

	public Contrast(final double x1, final double x2, final double y1,
			final double y2) {

		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	@Override
	public void operate(final Canvas original) {

		if (!(x1 < x2) || !(y1 < y2)) {
			throw new IllegalArgumentException(
					"The params are incorrect because they have no order");
		}

		for (int i = 0; i < original.size; i++) {
			original.red[i] = contrast(original.red[i]);
			original.green[i] = contrast(original.green[i]);
			original.blue[i] = contrast(original.blue[i]);
		}
	}

	private double contrast(final double d) {

		double m = 0;
		double b = 0;
		if (d < x1) {
			m = y1 / x1;
			b = 0;
		} else if (d > x2) {
			m = (255 - y2) / (255 - x2);
			b = y2 - m * x2;
		} else {
			m = (y2 - y1) / (x2 - x1);
			b = y1 - m * x1;
		}
		return m * d + b;
	}

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 3));

		panel.add(new JLabel("X1: "));
		final JSlider x1f = new JSlider(0, (int) x2, (int) x1);
		panel.add(x1f);
		final JLabel x1v = new JLabel(Double.toString(x1));
		panel.add(x1v);
		panel.add(new JLabel("X2: "));
		final JSlider x2f = new JSlider((int) x1, 255, (int) x2);
		panel.add(x2f);
		final JLabel x2v = new JLabel(Double.toString(x2));
		panel.add(x2v);
		panel.add(new JLabel("Y1: "));
		final JSlider y1f = new JSlider(0, (int) y2, (int) y1);
		panel.add(y1f);
		final JLabel y1v = new JLabel(Double.toString(y1));
		panel.add(y1v);
		panel.add(new JLabel("Y2: "));
		final JSlider y2f = new JSlider((int) y1, 255, (int) y2);
		panel.add(y2f);
		final JLabel y2v = new JLabel(Double.toString(y2));
		panel.add(y2v);

		x1f.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newx1 = x1f.getValue();
				x1 = newx1;
				x2f.setMinimum((int) x1);
				x1v.setText(Double.toString(x1));
				didChange();
			}
		});
		x2f.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newx2 = x2f.getValue();
				x2 = newx2;
				x1f.setMaximum((int) x2);
				x2v.setText(Double.toString(x2));
				didChange();
			}
		});
		y1f.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newy1 = y1f.getValue();
				y1 = newy1;
				y2f.setMinimum((int) y1);
				y1v.setText(Double.toString(y1));
				didChange();
			}
		});
		y2f.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newy2 = y2f.getValue();
				y2 = newy2;
				y1f.setMaximum((int) y2);
				y2v.setText(Double.toString(y2));
				didChange();
			}
		});

		return panel;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("Contrast from ")
				.append(x1).append(",").append(y1)
				.append(" to ")
				.append(x2).append(",").append(y2)
				.toString();
	}
}
