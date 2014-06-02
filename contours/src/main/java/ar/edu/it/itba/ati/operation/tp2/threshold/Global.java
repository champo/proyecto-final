package ar.edu.it.itba.ati.operation.tp2.threshold;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.Threshold;
import ar.edu.it.itba.ati.operation.tp1.ToGray;

public class Global extends Operation {

	@Override
	public void operate(final Canvas original) {

		new ToGray().operate(original);

		int t = 123;
		int delta = Integer.MAX_VALUE;

		double white, black;
		int whiteCount, blackCount;

		while (delta > 5) {

			white = 0;
			black = 0;

			whiteCount = 0;
			blackCount = 0;

			for (int i = 0; i < original.size; i++) {

				if (original.red[i] < t) {
					black += original.red[i];
					blackCount++;
				} else {
					white += original.red[i];
					whiteCount++;
				}
			}

			int newT = (int) (((white / whiteCount) + (black / blackCount)) / 2);
			delta = Math.abs(t - newT);

			t = newT;
		}

		System.out.println(t);
		new Threshold(t).operate(original);
	}

}
