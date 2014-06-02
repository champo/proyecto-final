package ar.edu.it.itba.ati.operation.tp4;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public abstract class BasicHarrisOperation extends Operation {

	protected final int radius;
	protected final double sigma;
	protected final double h;

	public BasicHarrisOperation(final int radius, final double sigma, final double h) {
		this.radius = 5;
		this.sigma = 0.8;
		this.h = h; // 0.06
	}

	private double[] grad(final double image[], final int width, final int height, final int x,
			final int y) {
				int v00=0;
				int v01=0;
				int v02=0;
				int v10=0, v12=0, v20=0, v21=0, v22=0;

				int x0 = x-1;
				final int x1 = x;
				int x2 = x+1;
				int y0 = y-1;
				final int y1 = y;
				int y2 = y+1;
				if (x0<0) {
					x0=0;
				}
				if (y0<0) {
					y0=0;
				}
				if (x2>=width) {
					x2=width-1;
				}
				if (y2>=height) {
					y2=height-1;
				}

				v00=(int) image[x0 + y0 * width];
				v01=(int) image[x0 + y1 * width];
				v02=(int) image[x0 + y2 * width];
				v10=(int) image[x1 + y0 * width];
				v12=(int) image[x1 + y2 * width];
				v20=(int) image[x2 + y0 * width];
				v21=(int) image[x2 + y1 * width];
				v22=(int) image[x2 + y2 * width];

				final double sx = v20+2*v21+v22-(v00+2*v01+v02);
				final double sy = v02+2*v12+v22-(v00+2*v10+v20);
				return new double[] {sx/4,sy/4};
			}

	protected double[][] calculateHarris(final Canvas original, final double[] layer, final int width,
			final int height) {
				for (int i = 1; i < width - 1; i++) {
					for (int j = 1; j < height - 1; j++) {
						final int pos = j * width + i;
						layer[pos] = (original.red[pos] + original.green[pos] + original.blue[pos])/3;
					}
				}
				final double Iyy[][] = new double[width][height];
				final double Ixy[][] = new double[width][height];
				final double Ixx[][] = new double[width][height];

				final double grad[][][] = new double[width][height][];

				final double gauss[][] = new double[radius * 2 + 1][radius * 2 + 1];

				for (int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {
						grad[i][j] = grad(layer, width, height, i, j);
					}
				}

				double gausssum = 0;
				for (int j = -radius; j <= radius; j++) {
					for (int i = -radius; i <= radius; i++) {
						final double g = gaussian(i, j, sigma);
						gauss[i+radius][j+radius] = g;
						gausssum += g;
					}
				}
				for (int y=0; y < height; y++) {
					for (int x=0; x < width; x++) {

						for (int dy = -radius; dy <= radius; dy++) {
							for (int dx = -radius; dx <= radius; dx++) {
								final int xk = x + dx;
								final int yk = y + dy;
								if (xk<0 || xk >= width) {
									continue;
								}
								if (yk<0 || yk >= height) {
									continue;
								}

								// filter weight
								final double f = gauss[dx+radius][dy+radius];

								// convolution
								Ixx[x][y]+=f*grad[xk][yk][0]*grad[xk][yk][0];
								Iyy[x][y]+=f*grad[xk][yk][1]*grad[xk][yk][1];
								Ixy[x][y]+=f*grad[xk][yk][0]*grad[xk][yk][1];
							}
						}
						Ixx[x][y]/=gausssum;
						Ixy[x][y]/=gausssum;
						Iyy[x][y]/=gausssum;
					}
				}
				final double[][] harrismap = new double[width][height];
				double max=0;

				// for each pixel in the image
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						final double m00 = Ixx[x][y];
						final double m01 = Ixy[x][y];
						final double m10 = Ixy[x][y];
						final double m11 = Iyy[x][y];
						harrismap[x][y] = m00*m11 - m01*m10 - h*(m00+m11)*(m00+m11);

						if (harrismap[x][y] > max) {
							max = harrismap[x][y];
							System.out.println(max);
						}
					}
				}
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						double h=harrismap[x][y];
						if (h<0) {
							h=0;
						} else {
							h = Math.log(1+h) / Math.log(1+max);
						}
						harrismap[x][y]=h;
					}
				}
				return harrismap;
			}

	private double gaussian(final double x, final double y, final double sigma2) {
		final double t = (x*x+y*y)/(2*sigma2);
		final double u = 1.0/(2*Math.PI*sigma2);
		final double e = u*Math.exp( -t );
		return e;
	}

}
