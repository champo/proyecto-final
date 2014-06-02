package ar.edu.it.itba.ati.operation.tp3;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class HysteresisThreshold extends Operation {

	private int t1;
	private int t2;

	public HysteresisThreshold(final int t1, final int t2) {
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public void operate(final Canvas original) {

		boolean[] isBorder = new boolean[original.red.length];
		Deque<Integer> borders = new ArrayDeque<Integer>();
		for (int i = 0; i < original.size; i++) {

			if (original.red[i] > t2) {
				borders.push(i);
			}
		}


		while (!borders.isEmpty()) {
			int i = borders.pop();
			isBorder[i] = true;

			if (i - 1 >= 0 && original.red[i - 1] >= t1 && !isBorder[i - 1]) {
				borders.push(i - 1);
			}

			if (i + 1 < original.size && original.red[i + 1] >= t1 && !isBorder[i + 1]) {
				borders.push(i + 1);
			}

			if (i - original.width >= 0 && original.red[i - original.width] >= t1 && !isBorder[i - original.width]) {
				borders.push(i - original.width);
			}

			if (i + original.width < original.size && original.red[i + original.width] >= t1 && !isBorder[i + original.width]) {
				borders.push(i + original.width);
			}
/*
			if (i - 1 - original.width >= 0 && original.red[i - 1 - original.width] >= t1 && !isBorder[i - 1 - original.width]) {
				borders.push(i - 1 - original.width);
			}

			if (i + 1 + original.width < original.size && original.red[i + 1 + original.width] >= t1 && !isBorder[i + original.width + 1]) {
				borders.push(i + 1 + original.width);
			}

			if (i - original.width + 1 >= 0 && original.red[i - original.width + 1] >= t1 && !isBorder[i - original.width + 1]) {
				borders.push(i - original.width + 1);
			}

			if (i + original.width - 1 < original.size && original.red[i + original.width - 1] >= t1 && !isBorder[i + original.width - 1]) {
				borders.push(i + original.width - 1);
			}
*/
		}

		for (int i = 0; i < original.size; i++) {
			original.red[i] = isBorder[i] ? 255 : 0;
			original.green[i] = isBorder[i] ? 255 : 0;
			original.blue[i] = isBorder[i] ? 255 : 0;
		}

	}

	@Override
	public String toString() {
		return "Umbral (Histeresis) - " + t1 + " / " + t2;
	};

	@Override
	public Container getPanel() {
		final JPanel panel = new JPanel(new GridLayout(0, 2));

		panel.add(new JLabel("Menor:"));
		final JSlider low = new JSlider(0, 255, t1);
		panel.add(low);

		low.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final int newC = low.getValue();
				t1 = newC;
				didChange();
			}
		});

		panel.add(new JLabel("Mayor:"));
		final JSlider high = new JSlider(0, 255, t2);
		panel.add(high);

		high.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final int newC = high.getValue();
				t2 = newC;
				didChange();
			}
		});

		return panel;
	}
}
