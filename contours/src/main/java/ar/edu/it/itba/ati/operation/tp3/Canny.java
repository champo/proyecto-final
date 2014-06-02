package ar.edu.it.itba.ati.operation.tp3;

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
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.ToGray;
import ar.edu.it.itba.ati.operation.tp2.filter.GaussianFilter;

public class Canny extends Operation {

	private int t1;
	private int t2;
	private int radius;

	public Canny() {
		this.t1 = 70;
		this.t2 = 120;
		this.radius = 3;
	}

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);

		double sigma = 0.3 * (2 * radius + 1);

		new GaussianWithoutNormalization(original.width, original.height, radius, sigma).operate(original);
		new NotMaximumSupression().operate(original);
		new HysteresisThreshold(t1, t2).operate(original);

	}

	class GaussianWithoutNormalization extends GaussianFilter {

		public GaussianWithoutNormalization(final int width, final int height, final int radius, final double sigma) {
			super(0, 0, width, height, radius, sigma);
		}

		@Override
		protected double normalize(final double value, final double max_value, final double min_value) {
			return value;
		}

	}

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
			}
		});

		panel.add(new JLabel("Radio filtro:"));
		final JSlider filter = new JSlider(0, 10, radius);
		filter.setSnapToTicks(true);
		panel.add(filter);

		filter.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(final ChangeEvent arg0) {
				final int newC = filter.getValue();
				radius = newC;
			}
		});

		JButton go = new JButton("Update");
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
