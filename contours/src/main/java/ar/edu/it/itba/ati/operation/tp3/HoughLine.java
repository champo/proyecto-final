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

public class HoughLine extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);
		new RobertsDetector().operate(original);
		new Global().operate(original);

		final int angleSlots = 90;
		final double thetaStep = Math.PI / angleSlots;
		final double rhoStep = 1;

		final int D = Math.max(original.height, original.width);
		final double rhoRange = Math.pow(2, 0.5) * D;
		final int rhoCount = (int) Math.ceil(2 * rhoRange / rhoStep);

		final int[][] accum = new int[angleSlots + 1][rhoCount];

		for (int i = 0; i < original.width; i++) {
			for (int j = 0; j < original.height; j++) {
				final double value = original.red[i + j * original.width];
				if (value > 0) {

					for (int angleSlot = -angleSlots / 2; angleSlot <= angleSlots / 2; angleSlot++) {
						final double rho = i * Math.cos(angleSlot * thetaStep) + j * Math.sin(angleSlot * thetaStep);


						if (Math.abs(rho) <= rhoRange) {
							final int rhoSlot = (int) Math.floor((rho + rhoRange) / rhoStep);

							accum[angleSlot + angleSlots / 2][rhoSlot]++;
						}
					}

				}
			}
		}


		final List<Line> lines = new ArrayList<Line>();
		for (int i = 0; i <= angleSlots; i++) {
			for (int j = 0; j < rhoCount; j++) {
				if (accum[i][j] > 0) {
					lines.add(new Line(i, j, accum[i][j]));
				}
			}
		}
		Collections.sort(lines, new Comparator<Line>() {

			@Override
			public int compare(final Line arg0, final Line arg1) {
				return arg1.score - arg0.score;
			}
		});

		for (int i = 0; i < original.width; i++) {
			for (int j = 0; j < original.height; j++) {
				original.red[i + j * original.width] = 0;
				original.green[i + j * original.width] = 0;
				original.blue[i + j * original.width] = 0;
			}
		}

		final int threshold = (int) (lines.get(0).score * 0.3);
		for (int line = 0; line < 10; line++) {
			final Line p = lines.get(line);
			if (p.score < threshold) {
				break;
			}

			final double angle = (p.angleSlot - angleSlots / 2) * thetaStep;
			final double rho = p.rhoSlot * rhoStep - rhoRange;

			for (int i = 0; i < original.width; i++) {
				for (int j = 0; j < original.height; j++) {
					if (Math.abs(i * Math.cos(angle) + j * Math.sin(angle) - rho) <= rhoStep) {
						original.red[i + j * original.width] = 255;
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
