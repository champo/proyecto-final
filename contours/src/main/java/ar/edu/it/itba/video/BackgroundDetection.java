package ar.edu.it.itba.video;


public class BackgroundDetection extends AbstractFrameProviderDecorator {

	private static final double THRESHOLD_ENERGY = 25.0;
	private static final double THRESHOLD_FIRST_PASS = 25.0;
	private static final double BETA = 0.4;

	private static final int MAX_HIST = 1000;
	private static final int MIN_STD = 10;
	private static final int STDS_OUT = 4;
	private static final int WINDOW_SIZE = 7;
	private static final int WINDOW_THRESHOLD = 25;
	private static final int WINDOW_HALF_SIZE = WINDOW_SIZE/2;

	private int wSize;
	private int frameCount;

	private double[][] cummEnergy;
	private double[][] currentEnergy;
	private boolean[][] finalForeground;

	private boolean[][] empty;
	private boolean[][] isForeground;

	private double[][] expected;
	private double[][] std;

	private double mean[][];
	private double M2[][];

	private boolean firstRound;

	// For debugging purposes
	private static final double BIN_SIZE = 15;
	private double[] histogram;
	private int[] cases;

	public void printData(int x, int y) {
		System.out.println("Point " + x + ", " + y + ", empty = " + empty[x][y] + ", expected = " + expected[x][y] + ", std = " + std[x][y]);
		System.out.println("Energy: " + cummEnergy[x][y] + ", mean = " + mean[x][y]);
	}

	public BackgroundDetection(final FrameProvider provider, final int wSize) {
		super(provider);
		this.wSize = wSize;
		this.firstRound = true;
	}

	@Override
	public void nextFrame() {
		super.nextFrame();
		if (expected == null) {
			// Lazy init everything
			initializeAll();
			setupBaseModel();
		}
		frameCount++;
		analyzeFrame();
		if (frameCount == wSize) {
			calculateNewModel();
			resetForNewWindow();
			setupBaseModel();
		}
	}

	private void analyzeFrame() {
		// Update online variance values
		for (int i = 0; i < provider.getWidth(); i++) {
			for (int j = 0; j < provider.getHeight(); j++) {
				double intensity = getInstensity(provider.getRGB(i, j));
				double delta = intensity - mean[i][j];
				mean[i][j] = mean[i][j] + delta / frameCount;
				M2[i][j] += delta * (intensity - mean[i][j]);
				double energy = Math.pow(intensity - expected[i][j], 2);
				currentEnergy[i][j] = energy;
				cummEnergy[i][j] += energy;
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

	private void setupBaseModel() {

		for (int i = 0; i < provider.getWidth(); i++) {
			for (int j = 0; j < provider.getHeight(); j++) {
				double intensity = getInstensity(provider.getRGB(i, j));
				expected[i][j] = intensity;
			}
		}
	}

	private void blackoutBackground() {
		for (int i = 0; i < provider.getWidth(); i++) {
			for (int j = 0; j < provider.getHeight(); j++) {
				double intensity = getInstensity(provider.getRGB(i, j));
				isForeground[i][j] = isForeground(intensity, i, j);
			}
		}
		// Moving WINDOW_SIZE x WINDOW_SIZE frame. If less than WINDOW_THRESHOLD out of them are
		// considered foreground, then set frame then set frame to black
		for (int i = 0; i < provider.getWidth(); i++) {
			int count = 0;
			// Start counting the first columns. Out of bounds are treated as background
			for (int deltaI = -WINDOW_HALF_SIZE; deltaI <= WINDOW_HALF_SIZE; deltaI++) {
				if (i + deltaI >= 0 && i + deltaI < provider.getWidth()) {
					// Strictly less; to simplify counting in the next loop
					for (int deltaJ = 0; deltaJ < WINDOW_HALF_SIZE; deltaJ++) {
						count += isForeground[i+deltaI][deltaJ] ? 1 : 0;
					}
				}
			}
			// Go through the whole row.
			for (int j = 0; j < provider.getHeight(); j++) {
				// Add the new column
				if (j + WINDOW_HALF_SIZE < provider.getHeight()) {
					for (int deltaI = -WINDOW_HALF_SIZE; deltaI <= WINDOW_HALF_SIZE; deltaI++) {
						if (i+deltaI >= 0 && i + deltaI < provider.getWidth()) {
							count += isForeground[i+deltaI][j+WINDOW_HALF_SIZE] ? 1 : 0;
						}
					}
				}
				finalForeground[i][j] = count >= WINDOW_THRESHOLD;
				// Remove the count for the leftmost column before incrementing j
				if (j - WINDOW_HALF_SIZE >= 0) {
					for (int deltaI = -WINDOW_HALF_SIZE; deltaI <= WINDOW_HALF_SIZE; deltaI++) {
						if (i+deltaI >= 0 && i + deltaI < provider.getWidth()) {
							count -= isForeground[i+deltaI][j-WINDOW_HALF_SIZE] ? 1 : 0;
						}
					}
				}
			}
		}
	}
	@Override
	public int getRGB(int x, int y) {
		double intensity = getInstensity(provider.getRGB(x, y));
		if (!finalForeground[x][y]) {
			return 0; 
		}
		return provider.getRGB(x, y);
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
				isForeground[i][j] = false;
			}
		}
		firstRound = false;
		frameCount = 0;
	}

	private void initializeAll() {

		int width = provider.getWidth();
		int height = provider.getHeight();
		cummEnergy = new double[width][height];
		currentEnergy  = new double[width][height];
		empty = new boolean[width][height];
		isForeground = new boolean[width][height];
		finalForeground = new boolean[width][height];
		std = new double[width][height];
		cases = new int[5];

		mean = new double[width][height];
		expected = new double[width][height];
		M2 = new double[width][height];

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				expected[i][j] = getInstensity(provider.getRGB(i, j));
			}
		}
	}

	private void calculateNewModel() {histogram = new double[MAX_HIST];
		for (int i = 0; i < cummEnergy.length; i++) {
			for (int j = 0; j < cummEnergy[i].length; j++) {
				if (cummEnergy[i][j]/BIN_SIZE < MAX_HIST) {
					histogram[(int) (cummEnergy[i][j]/BIN_SIZE)]++;
				}
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
						expected[i][j] = BETA * expected[i][j] + (1 - BETA)
								* mean[i][j];
						std[i][j] = BETA * std[i][j] + (1 - BETA) * currStd;
					}
					empty[i][j] = false;
				}
			}
		}
		int max = cummEnergy.length * cummEnergy[0].length;
		System.out.println("Histogram for " + max + " is:");
		double cummEnergy = 0;
		for (int i = 0; i < MAX_HIST; i++) {
			cummEnergy += histogram[i];
			if (histogram[i] / max > 0.0001) {
				System.out.println("Points with energy in range " + i
						* BIN_SIZE / wSize + " to " + (i + 1) * BIN_SIZE / wSize + " = "
						+ histogram[i] + "(" + 100 * histogram[i] / max + "%, cumm = " + cummEnergy / max * 100 + ")");
			}
		}
	}
}
