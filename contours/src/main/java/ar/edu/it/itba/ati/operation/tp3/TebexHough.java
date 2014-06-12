package ar.edu.it.itba.ati.operation.tp3;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.ToGray;
import ar.edu.it.itba.ati.operation.tp2.edge.RobertsDetector;
import ar.edu.it.itba.ati.operation.tp2.threshold.Global;

public class TebexHough extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);
		new RobertsDetector().operate(original);
		// new Global().operate(original);

		final int threshold = 15;
		final int[][] deltas = new int[][] {
				{0, 0}, {0, 1}, {1, 0}, {-1, 0}, {0, -1},
				{1, 1}, {1, -1}, {-1, 1}, {-1, -1}
				// {0, 2}, {2, 0}, {-2, 0}, {0, -2}
		};
		double[] newArray = new double[original.size];
		for (int i = 0; i < original.width; i++) {
			for (int j = 0; j < original.height; j++) {
				int show = 0;
				for (final int[] delta : deltas) {
					final int index = i + delta[0] + (j+delta[1]) * original.width;
					if (index >= 0 && index < original.red.length && original.red[index] > threshold) {
						show++;
					}
				}
				if (show > 0) {
					newArray[i + j * original.width] = 255;
				} else {
					newArray[i + j * original.width] = 0;
				}
			}
		}
		original.red = newArray;
		
		final boolean[] seenPoints = new boolean[original.size];
		final int[] intDeltas = {1, -1, original.width, -original.width,
				1 + original.width, 1 - original.width, original.width - 1, -(original.width + 1)};
		for (int i = 0; i < original.width; i++) {
			for (int j = 0; j < original.height; j++) {
				final int index = i + j * original.width;
				if (original.red[index] != 0 && !seenPoints[index]) {
					final Deque<Integer> deque = new LinkedList<Integer>();
					deque.add(index);
					final Set<Integer> seen = new HashSet<Integer>();
					int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
					int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
					seenPoints[index] = true;
					seen.add(index);
					while (!deque.isEmpty()) {
						final Integer number = deque.poll();
						minX = Math.min(minX, number % original.width);
						maxX = Math.max(maxX, number % original.width);
						minY = Math.min(minY, number / original.width);
						maxY = Math.max(maxY, number / original.width);
						for (final int intDelta : intDeltas) {
							final int neighbor = number + intDelta;
							if (neighbor >= 0 && neighbor < original.size && original.red[neighbor] != 0 && !seenPoints[neighbor]) {
								deque.add(neighbor);
								seenPoints[neighbor] = true;
								seen.add(neighbor);
							}
						}
					}
					if (seen.size() > 60 && maxX - minX > 40 && maxY - minY > 40) {
						for (final Integer point : seen) {
							original.red[point] = 255;
						}
					} else {
						for (final Integer point : seen) {
							original.red[point] = 0;
						}
					}
				}
			}
		}
	}

	private static class Line {
		public Line(final int angleSlot, final int rhoSlot, final int score) {
			this.angleSlot = angleSlot;
			this.rhoSlot = rhoSlot;
			this.score = score;
		}

		int angleSlot;
		int rhoSlot;

		int score;
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
