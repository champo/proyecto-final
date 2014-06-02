package ar.edu.it.itba.ati.operation;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ar.edu.it.itba.ati.Canvas;

public abstract class BaseMask extends Operation {

	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int radius;

	public BaseMask(final int x, final int y, final int width, final int height,
			final int radius) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.radius = radius;
	}

	@Override
	public void operate(final Canvas original) {
		operateOnLayer(original.red, original.width, original.height);
		operateOnLayer(original.green, original.width, original.height);
		operateOnLayer(original.blue, original.width, original.height);
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	protected abstract double[][] buildMask();

	protected void operateOnLayer(final double[] array, final int width, final int height) {

		final double[] newArray = Arrays.copyOf(array, array.length);

		double[][] mask = buildMask();

		double min_value = Double.POSITIVE_INFINITY;
		double max_value = Double.NEGATIVE_INFINITY;
		for (int win_x = x; win_x < x + this.width; win_x++) {
			for (int win_y = y; win_y < y + this.height; win_y++) {

				final int win_pos = win_x + win_y * width;
				double accum = 0;

				for (int mask_x = 0; mask_x < mask[0].length; mask_x++) {
					for (int mask_y = 0; mask_y < mask.length; mask_y++) {

						int delta_x = win_x + mask_x - radius;
						int delta_y = win_y + mask_y - radius;

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

						accum += mask[mask_y][mask_x] * array[delta_pos];
					}
				}
				newArray[win_pos] = accum;
				if (accum < min_value) {
					min_value = accum;
				}
				if (accum > max_value) {
					max_value = accum;
				}
			}
		}


		for (int win_x = x; win_x < x + this.width; win_x++) {
			for (int win_y = y; win_y < y + this.height; win_y++) {

				final int win_pos = win_x + win_y * width;
				array[win_pos] = normalize(newArray[win_pos], max_value, min_value);
			}
		}
	}

	protected double normalize(final double value, final double max_value,
			final double min_value) {

		final double scale = (max_value > 255 ? 255 : max_value) / (max_value - min_value);
		double normalized;
		if (min_value < 0) {
			normalized = (value - min_value) * scale;
		} else {
			normalized = (value - min_value) * scale + min_value;
		}

		if (normalized < 0) {
			normalized = 0;
		} else if (normalized > 255) {
			normalized = 255;
		}

		return normalized;
	}

	public void setX(final int newX) {
		x = newX;
		didChange();
	}

	public void setY(final int newY) {
		y = newY;
		didChange();
	}

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));
		panel.add(new JLabel("X"));
		final TextField xfield = new TextField(Double.toString(x));
		panel.add(xfield);

		xfield.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					x = Double.valueOf(xfield.getText()).intValue();
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		panel.add(new JLabel("Y"));
		final TextField yfield = new TextField(Double.toString(y));
		panel.add(yfield);

		yfield.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					y = Double.valueOf(yfield.getText()).intValue();
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		panel.add(new JLabel("Width"));
		final TextField widthField = new TextField(Double.toString(width));
		panel.add(widthField);

		widthField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					width = Double.valueOf(widthField.getText()).intValue();
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		panel.add(new JLabel("Height"));
		final TextField heightField = new TextField(Double.toString(height));
		panel.add(heightField);

		heightField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					height = Double.valueOf(heightField.getText()).intValue();
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});

		panel.add(new JLabel("Radius"));
		final TextField radiusField = new TextField(Double.toString(radius));
		panel.add(radiusField);

		radiusField.addTextListener(new TextListener() {

			@Override
			public void textValueChanged(final TextEvent arg0) {

				try {
					radius = Double.valueOf(radiusField.getText()).intValue();
					didChange();
				} catch (final NumberFormatException e) {
				}
			}
		});
		return panel;
	}
}
