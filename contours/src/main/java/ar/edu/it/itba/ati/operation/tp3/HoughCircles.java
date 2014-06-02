package ar.edu.it.itba.ati.operation.tp3;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.ToGray;
import ar.edu.it.itba.ati.operation.tp2.edge.RobertsDetector;
import ar.edu.it.itba.ati.operation.tp2.threshold.Global;

public class HoughCircles extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);
		new RobertsDetector().operate(original);
		new Global().operate(original);

		final int step = 3;
		final int D = Math.max(original.height, original.width);

		final double range = Math.pow(2, 0.5) * D;
		final int dimensionCount = (int) Math.ceil(range / step);
		final int imageOffset = (int) Math.ceil((range - D) / (2 * step));
		final int end = dimensionCount - imageOffset - 1;

		final int radiusCount = D / (3 * step);

		final int[][][] accum = new int[dimensionCount][dimensionCount][radiusCount];

		for (int j = 0; j < original.height; j++) {
			for (int i = 0; i < original.width; i++) {
				if (original.red[i + j * original.width] == 0) {
					continue;
				}

				for (int a = -imageOffset; a < end; a++) {
					final double firstTerm = Math.pow(i - a * step, 2);
					for (int b = -imageOffset; b < end; b++) {
						final double radius = Math.pow(firstTerm + Math.pow(j - b * step, 2), 0.5);

						final int rLower = (int) Math.floor(radius);
						final int rUpper = (int) Math.ceil(radius);
						if (rLower / step != rUpper / step) {
							if (rUpper < radiusCount * step) {
								accum[a + imageOffset][b + imageOffset][rUpper / step]++;
							}
						}
						if (rLower < radiusCount * step) {
							accum[a + imageOffset][b + imageOffset][rLower / step]++;
						}
					}
				}
			}
		}


		final List<Circle> circles = new ArrayList<Circle>();
		for (int i = 0; i < dimensionCount; i++) {
			for (int j = 0; j < dimensionCount; j++) {
				for (int r = 0; r < radiusCount; r++) {
					if (accum[i][j][r] > 10) {
						circles.add(new Circle(i, j, r, accum[i][j][r]));
						System.out.println("Circulo en " + i + "," + j + " de radio " + r);
					}
				}
			}
		}
		Collections.sort(circles, new Comparator<Circle>() {

			@Override
			public int compare(final Circle arg0, final Circle arg1) {
				return arg1.score - arg0.score;
			}
		});

		for (int j = 0; j < original.height; j++) {
			for (int i = 0; i < original.width; i++) {
				original.red[i + j * original.width] = 0;
				original.green[i + j * original.width] = 0;
				original.blue[i + j * original.width] = 0;
			}
		}

		final int threshold = (int) (circles.get(0).score * 0.2);
		for (int circle = 0; circle < 10; circle++) {
			final Circle c = circles.get(circle);
			if (c.score < threshold) {
				break;
			}

			final int a = (c.aSlot - imageOffset) * step;
			final int b = (c.bSlot - imageOffset) * step;
			final int r_sq = c.radius * step * c.radius * step;

			for (int j = 0; j < original.height; j++) {
				final double firstTerm = Math.pow(j - b, 2);
				for (int i = 0; i < original.width; i++) {
					final double diff = Math.sqrt(Math.abs(firstTerm + Math.pow(i - a, 2) - r_sq));
					if (diff < 10) {
						original.red[i + j * original.width] = Math.max(0, 255 - Math.pow(diff, 2));
					}
				}
			}
		}


	}



	private static class Circle {
		public Circle(final int a, final int b, final int radius, final int score) {
			aSlot = a;
			bSlot = b;
			this.radius = radius;
			this.score = score;
		}

		int aSlot;
		int bSlot;
		int radius;

		int score;

		@Override
		public String toString() {
			return "Circle [aSlot=" + aSlot + ", bSlot=" + bSlot + ", radius="
					+ radius + ", score=" + score + "]";
		}


	}

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

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
