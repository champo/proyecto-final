package ar.edu.it.itba.video;

import java.awt.image.BufferedImage;

public class BackgroundDetection implements FrameProvider {

	// 1.9 falls in the 80th percentile
	// 3.0 falls about the 90th percentile
	// 8.0 falls in the 95th percentile
	// 20.0 falls in the 97.5th percentile
	private static final double THRESHOLD_ENERGY = 25.0;
	private static final double THRESHOLD_FIRST_PASS = 25;
	private static final double BETA = 0.4;

	private final FrameProvider provider;

	private int wSize;
	private int frameCount;

	private double[][] cummEnergy;
	private double[][] currentEnergy;

	private boolean[][] empty;
	private double[][] expected;
	private double[][] std;

	private double mean[][];
	private double M2[][];

	private boolean firstRound;

	// For debugging purposes
	private static final double BIN_SIZE = 15;
	private double[] histogram;
	private int[] cases;
	
	private static final int MAX_HIST = 50;
	private static final int MIN_STD = 10;
	private static final int STDS_OUT = 4;

	public void printData(int x, int y) {
		System.out.println("Point " + x + ", " + y + ", empty = " + empty[x][y] + ", expected = " + expected[x][y] + ", std = " + std[x][y]);
		System.out.println("Energy: " + cummEnergy[x][y] + ", mean = " + mean[x][y]);
	}

	public BackgroundDetection(final FrameProvider provider, final int wSize) {
		super();
		this.provider = provider;
		this.wSize = wSize;
		this.firstRound = true;
	}

	@Override
	public BufferedImage nextFrame() {
		BufferedImage frame = provider.nextFrame();
		if (expected == null) {
			// Lazy init everything
			initializeAll(frame);
			setupBaseModel(frame);
		}
		frameCount++;
		analyzeFrame(frame);
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
				double delta = intensity - mean[i][j];
				mean[i][j] = mean[i][j] + delta / frameCount;
				M2[i][j] += delta * (intensity - mean[i][j]);
				double energy = Math.pow(intensity - expected[i][j], 2);
				cummEnergy[i][j] += energy;
				currentEnergy[i][j] = energy;
			}
		}
	}

	private static double getInstensity(int rgb) {

		int red = (rgb >> 16) & 0xFF;
		int green = (rgb >> 8) & 0xFF;
		int blue= rgb & 0xFF;

		// WARNING: Altered formula to give red and blue more intensity.
		return 0.69 * red + 0.31 * blue;
		// Original is commented below.
		// return 0.30 * red + 0.59 * green + 0.11 * blue;
	}

	private void setupBaseModel(BufferedImage frame) {

		for (int i = 0; i < frame.getWidth(); i++) {
			for (int j = 0; j < frame.getHeight(); j++) {
				double intensity = getInstensity(frame.getRGB(i, j));
				expected[i][j] = intensity;
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
		if (intensity == 0) {
			return false;
		}
		if (!firstRound) {
			if (empty[i][j]) {
				return true;
			} else {
				boolean stdsOut = Math.abs(intensity - expected[i][j]) > STDS_OUT * std[i][j];
				if (std[i][j] > MIN_STD) {
					return stdsOut;
				} else {
					return currentEnergy[i][j] > THRESHOLD_ENERGY && stdsOut;
				}
			}
		} else {
			return Math.abs(intensity - expected[i][j]) > THRESHOLD_FIRST_PASS;
		}
	}

	private void resetForNewWindow() {
		cases = new int[5];
		for (int i = 0; i < cummEnergy.length; i++) {
			for (int j = 0; j < cummEnergy[i].length; j++) {
				mean[i][j] = 0;
				M2[i][j] = 0;
				cummEnergy[i][j] = 0;
			}
		}
		firstRound = false;
		frameCount = 0;
	}

	private void initializeAll(BufferedImage frame) {

		int width = frame.getWidth();
		int height = frame.getHeight();
		cummEnergy = new double[width][height];
		currentEnergy = new double[width][height];
		empty = new boolean[width][height];
		std = new double[width][height];
		cases = new int[5];

		mean = new double[width][height];
		expected = new double[width][height];
		M2 = new double[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				expected[i][j] = getInstensity(frame.getRGB(i, j));
			}
		}
	}

	private void calculateNewModel() {

		for (int i = 0; i < cummEnergy.length; i++) {
			for (int j = 0; j < cummEnergy[i].length; j++) {
				double currStd = Math.sqrt(M2[i][j]/(wSize - 1));
				if (cummEnergy[i][j] > THRESHOLD_ENERGY * wSize) {
					empty[i][j] = true;
					expected[i][j] = mean[i][j];
					std[i][j] = currStd;
				} else {
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
	}
}
