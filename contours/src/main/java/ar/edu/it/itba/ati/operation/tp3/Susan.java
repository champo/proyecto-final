package ar.edu.it.itba.ati.operation.tp3;

import java.awt.Container;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.ToGray;

public class Susan extends Operation {

	private static final double[][] mask = {
		{ 0, 0, 1, 1, 1, 0, 0 },
		{ 0, 1, 1, 1, 1, 1, 0 },
		{ 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1 },
		{ 1, 1, 1, 1, 1, 1, 1 },
		{ 0, 1, 1, 1, 1, 1, 0 },
		{ 0, 0, 1, 1, 1, 0, 0 },
	};

	private int t;

	private final boolean wantCorner;

	public Susan(final int t, final boolean wantCorner) {
		this.t = t;
		this.wantCorner = wantCorner;
	}

	@Override
	public void operate(final Canvas original) {
		new ToGray().operate(original);
		operateOnLayer(original.red, original.width, original.height);
		operateOnLayer(original.blue, original.width, original.height);
		operateOnLayer(original.green, original.width, original.height);
	}

	protected void operateOnLayer(final double[] array, final int width, final int height) {

		final double[] newArray = new double[array.length];

		for (int win_x = 0; win_x < width; win_x++) {
			for (int win_y = 0; win_y < height; win_y++) {

				final int win_pos = win_x + win_y * width;
				double centerValue = array[win_pos];
				int count = 0;

				for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
					for (int mask_y = 0; mask_y < mask.length; mask_y++) {

						int delta_x = win_x + mask_x - 3;
						int delta_y = win_y + mask_y - 3;

						if (delta_x < 0) {
							delta_x = 0;
						}
						if (delta_x >= width) {
							delta_x = width - 1;
						}
						if (delta_y < 0) {
							delta_y = 0;
						}
						if (delta_y >= height) {
							delta_y = height - 1;
						}

						final int delta_pos = delta_x + delta_y * width;

						if (mask[mask_y][mask_x] != 0 && Math.abs(array[delta_pos] - centerValue) < t) {
							count++;
						}

					}
				}

				double s = 1 - (double) count / (3 + 5 + 3 * 7 + 5 + 3);

				if (wantCorner && s > 0.60) {
					markCorner(array, newArray, width, height, win_x, win_y);
				} else if (!wantCorner && Math.abs(s - 0.5) < 0.15) {
					newArray[win_pos] = 255;
				}

			}
		}

		for (int i = 0; i < array.length; i++) {
			array[i] = newArray[i];
		}
	}

	private void markCorner(final double[] array, final double[] newArray, final int width, final int height, final int win_x, final int win_y) {
		double centerValue = array[win_x + win_y * width];

		for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
			for (int mask_y = 0; mask_y < mask.length; mask_y++) {

				int delta_x = win_x + mask_x - 3;
				int delta_y = win_y + mask_y - 3;

				if (delta_x < 0) {
					delta_x = 0;
				}
				if (delta_x >= width) {
					delta_x = width - 1;
				}
				if (delta_y < 0) {
					delta_y = 0;
				}
				if (delta_y >= height) {
					delta_y = height - 1;
				}

				final int delta_pos = delta_x + delta_y * width;

				if (mask[mask_y][mask_x] != 0 && Math.abs(array[delta_pos] - centerValue) < t) {
					newArray[win_x + win_y * width] = 255;
				}

			}
		}
	}

	@Override
	public String toString() {
		return "Susan - " + t;
	}

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

		panel.add(new JLabel("Umbral:"));
		final JSlider uf = new JSlider(0, 255, t);
		panel.add(uf);

		uf.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final double newC = uf.getValue();
				t = (int) newC;
				didChange();
			}
		});

		return panel;
	}

}
