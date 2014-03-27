package ar.edu.it.itba.video;

import java.awt.image.BufferedImage;

public class BackgroundDetection implements FrameProvider {

	private static final double THRESHOLD_ENERGY = 10;
	private static final double THRESHOLD_FIRST_PASS = 25;
	private static final double BETA = 0.1;

	private final FrameProvider provider;

	private int wSize;
	private int frameCount;

	private double[][] bgModel;
	private double[][] energy;

	private boolean[][] empty;
	private double[][] expected;
	private double[][] std;

	private double mean[][];
	private double variance[][];
	private double delta[][];
	private double M2[][];

	private boolean firstRound;

	// For debugging purposes
	private static final double BIN_SIZE = 1000;
	private double[] histogram;

	public BackgroundDetection(final FrameProvider provider, final int wSize) {
		super();
		this.provider = provider;
		this.wSize = wSize;
		this.firstRound = true;
	}

	@Override
	public BufferedImage nextFrame() {
		BufferedImage frame = provider.nextFrame();
		if (bgModel == null) {
			// Lazy init everything
			initializeAll(frame);
			setupBaseModel(frame);
		}
		analyzeFrame(frame);
		frameCount++;
		if (frameCount == wSize) {
			calculateNewModel();
			resetForNewWindow();
			setupBaseModel(frame);
		}
		return blackoutBackground(frame);
	}

	private void analyzeFrame(BufferedImage frame) {
		// Update online variance values
		for (int i = 0; i < frame.getWidth(); i++) {
			for (int j = 0; j < frame.getHeight(); j++) {
				double intensity = getInstensity(frame.getRGB(i, j));
				delta[i][j] = intensity - variance[i][j];
				variance[i][j] = variance[i][j] + delta[i][j];
				M2[i][j] = M2[i][j] + delta[i][j] * (intensity - variance[i][j]);
				energy[i][j] += Math.pow(intensity - bgModel[i][j], 2);
			}
		}
	}

	private static double getInstensity(int rgb) {

		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue= rgb & 0xFF;

		// Testing using hue instead of intensity: uncomment this
		// return getHueValue(red, green, blue);
		return 0.30 * red + 0.59 * green + 0.11 * blue;
	}

	private void setupBaseModel(BufferedImage frame) {

		for (int i = 0; i < frame.getWidth(); i++) {
			for (int j = 0; j < frame.getHeight(); j++) {
				double intensity = getInstensity(frame.getRGB(i, j));
				bgModel[i][j] = intensity;
			}
		}
	}

	private BufferedImage blackoutBackground(BufferedImage frame) {

		for (int i = 0; i < frame.getWidth(); i++) {
			for (int j = 0; j < frame.getHeight(); j++) {
				double intensity = getInstensity(frame.getRGB(i, j));
				if (!isForeground(intensity, i, j)) {
					frame.setRGB(i, j, 0);
				}
			}
		}
		return frame;
	}

	private boolean isForeground(double intensity, int i, int j) {
		if (!firstRound) {
			if (empty[i][j]) {
				return (intensity - expected[i][j]) > 2 * std[i][j];
			} else {
				return true;
			}
		} else {
			return Math.abs(intensity - bgModel[i][j]) > THRESHOLD_FIRST_PASS;
		}
	}

	private void resetForNewWindow() {
		for (int i = 0; i < energy.length; i++) {
			for (int j = 0; j < energy[i].length; j++) {
				delta[i][j] = 0;
				variance[i][j] = 0;
				M2[i][j] = 0;
				energy[i][j] = 0;
			}
		}
		firstRound = false;
	}

	private void initializeAll(BufferedImage frame) {

		int width = frame.getWidth();
		int height = frame.getHeight();
		energy = new double[width][height];
		empty = new boolean[width][height];
		std = new double[width][height];
		bgModel = new double[width][height];

		mean = new double[width][height];
		expected = new double[width][height];
		variance = new double[width][height];
		delta = new double[width][height];
		M2 = new double[width][height];
		histogram = new double[1000];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				expected[i][j] = getInstensity(frame.getRGB(i, j));
			}
		}
	}

	private void calculateNewModel() {

		for (int i = 0; i < 40; i++) {
			histogram[i] = 0;
		}
		for (int i = 0; i < energy.length; i++) {
			for (int j = 0; j < energy[i].length; j++) {
				histogram[(int)(energy[i][j] / BIN_SIZE / wSize)]++;
				if (energy[i][j] > THRESHOLD_ENERGY * wSize) {
					empty[i][j] = true;
				} else {
					double currStd = Math.sqrt(M2[i][j]/(wSize - 1));
					if (firstRound || empty[i][j]) {
						expected[i][j] = mean[i][j];
						std[i][j] = currStd;
					} else {
						// BETA is always < 0.5, so 1-BETA is bigger than BETA
						expected[i][j] = BETA * expected[i][j] + (1-BETA) * mean[i][j];
						std[i][j] = BETA * std[i][j] + (1-BETA) * currStd;
					}
					empty[i][j] = false;
				}
			}
		}
		for (int i = 0; i < 40; i++) {
			System.out.println("Histogram[" + i * BIN_SIZE + " to " + (i + 1) * BIN_SIZE + "] = "
					+ (100.0 * histogram[i] / (energy.length * energy[0].length)));
		}
	}

	private static int getHueValue(int red, int green, int blue) {
		float min;    //Min. value of RGB
		float max;    //Max. value of RGB
		float delMax; //Delta RGB value

		final float r = red / 255.0f;
		final float g = green / 255.0f;
		final float b = blue / 255.0f;

		if (r > g) {
			min = g;
			max = r;
		} else {
			min = r;
			max = g;
		}
		if (b > max) {
			max = b;
		}
		if (b < min) {
			min = b;
		}

		delMax = max - min;

		float H = 0;

		if ( delMax == 0 ) {
			H = 0;
		} else {
			if ( r == max ) {
				H = (g - b) / delMax % 6;
			} else if ( g == max ) {
				H = 2 + (b - r) / delMax;
			} else if ( b == max ) {
				H = 4 +  (r - g) / delMax;
			}

			H *= 60;
		}
		return (int) (H * 255.0f / 360.0f);
	}
}
