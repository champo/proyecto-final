package ar.edu.it.itba.ati.operation.tp4;

import ar.edu.it.itba.ati.Canvas;

public class HarrisResponse extends BasicHarrisOperation {

	public HarrisResponse(final int radius, final double sigma, final double h) {
		super(radius, sigma, h);
	}

	private double[] hslToRgb(final double x, final double y, final double z) {
	    double calcR = 0;
	    double calcG = 0;
	    double calcB = 0;
	    if (y == 0) { // saturation == 0
	      calcR = calcG = calcB = z;
	    } else {
	      final double which = (x - (int)x) * 6.0f;
	      final double f = which - (int)which;
	      final double p = z * (1.0f - y);
	      final double q = z * (1.0f - y * f);
	      final double t = z * (1.0f - y * (1.0f - f));

	      switch ((int)which) {
	        case 0:
	          calcR = z;
	          calcG = t;
	          calcB = p;
	          break;
	        case 1:
	          calcR = q;
	          calcG = z;
	          calcB = p;
	          break;
	        case 2:
	          calcR = p;
	          calcG = z;
	          calcB = t;
	          break;
	        case 3:
	          calcR = p;
	          calcG = q;
	          calcB = z;
	          break;
	        case 4:
	          calcR = t;
	          calcG = p;
	          calcB = z;
	          break;
	        case 5:
	          calcR = z;
	          calcG = p;
	          calcB = q;
	          break;
	      }
	    }

	    final int calcRi = (int)(255 * calcR);
	    final int calcGi = (int)(255 * calcG);
	    final int calcBi = (int)(255 * calcB);
	    return new double[] { calcRi, calcGi, calcBi };
	}

	@Override
	public void operate(final Canvas original) {

		final double layer[] = new double[original.blue.length];
		final int width = original.width;
		final int height = original.height;


		final double[][] harrismap = calculateHarris(original, layer, width,
				height);
		for (int i = 0; i < layer.length; i++) {
			final double colors[] = hslToRgb(0.37-harrismap[i%width][i/width]/3, 1, 0.75);
			original.red[i] = colors[0];
			original.green[i] = colors[1];
			original.blue[i] = colors[2];
		}
	}

}
