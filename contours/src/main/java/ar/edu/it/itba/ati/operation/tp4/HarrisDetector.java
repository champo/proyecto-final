package ar.edu.it.itba.ati.operation.tp4;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ar.edu.it.itba.ati.Canvas;

public class HarrisDetector extends BasicHarrisOperation {

	private int threshold;
	public HarrisDetector(final int threshold, final int radius, final double sigma, final double h) {
		super(radius, sigma, h);
		this.threshold = threshold;
	}

	@Override
	public void operate(final Canvas original) {

		final double layer[] = new double[original.blue.length];
		final int width = original.width;
		final int height = original.height;

		double[][] harrismap = calculateHarris(original, layer, width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (255 * harrismap[x][y] > threshold && isLocalMax(x, y, harrismap)) {
					original.red[y * width + x] = 255;
					original.green[y * width + x] = 255;
					original.blue[y * width + x] = 255;
				} else {
					original.red[y * width + x] = 0;
					original.green[y * width + x] = 0;
					original.blue[y * width + x] = 0;
				}
			}
		}
	}

	private boolean isLocalMax(final int x, final int y, final double[][] harrismap) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				if (dx == 0 && dy == 0) {
					continue;
				}
				if (x + dx >= 0 && y + dy >= 0) {
					if (x + dx < harrismap.length && y + dy < harrismap[0].length) {
						if (harrismap[x+dx][y+dy] >= harrismap[x][y]) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

		panel.add(new JLabel("Threshold:"));
		final JSlider thr = new JSlider(0, 255, threshold);
		panel.add(thr);

		thr.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final int newC = thr.getValue();
				threshold = newC;
			}
		});
		final JButton go = new JButton("Update");
		go.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				didChange();
			}
		});
		panel.add(go);

		return panel;
	}
}
