package ar.edu.it.itba.tracker;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

public class AdaptiveLinearPredictors {

	private static final int PIXELS_PER_SAMPLE = 16;

	private enum State {
		UNINITIALIZED,
		TRACKING,
		LOST
	}

	private State state = State.UNINITIALIZED;

	private final ObjectFrame initialFrame;

	private SimpleMatrix homography;

	private double[] previousSamples;

	private DenseMatrix64F predictor;

	private List<Point> subsets;

	private static Random random = new Random();

	public AdaptiveLinearPredictors(final ObjectFrame initialFrame) {
		super();
		this.initialFrame = initialFrame;
	}

	private List<Point> subsetsFromFrame(final ObjectFrame frame) {

		int startX = frame.getMinX();
		int startY = frame.getMinY();

		int endX = frame.getMaxX();
		int endY = frame.getMaxY();

		int[] xpoints = {
				frame.getFirst().x,
				frame.getSecond().x,
				frame.getFourth().x,
				frame.getThird().x,
				frame.getFirst().x
				};
		int[] ypoints = {
				frame.getFirst().y,
				frame.getSecond().y,
				frame.getFourth().y,
				frame.getThird().y,
				frame.getFirst().y
				};

		Polygon polygon = new Polygon(xpoints, ypoints, 5);

		List<Point> subsets = new ArrayList<Point>();

		for (int x = startX; x < endX; x += PIXELS_PER_SAMPLE) {
			for (int y = startY; y < endY; y += PIXELS_PER_SAMPLE) {
				if (polygon.contains(x, y)) {
					subsets.add(new Point(x, y));
				}
			}
		}

		return subsets;
	}

	public ObjectFrame trackFrame(final BufferedImage frame) {

		if (state == State.UNINITIALIZED) {

			trainPredictors(frame);
			state = State.TRACKING;

			return initialFrame;
		}

		DenseMatrix64F templateDelta = new DenseMatrix64F(8, 1);

		for (int i = 0; i < 3; i++) {
			double[] sampleDelta = sub(sample(frame, homography), previousSamples);

			DenseMatrix64F deltaMatrix = new DenseMatrix64F(sampleDelta.length, 1, true, sampleDelta);
			CommonOps.mult(predictor, deltaMatrix, templateDelta);
			homography = homography.mult(homography(templateDelta));
		}

		// TODO: Check error rate by checking against reference samples

		previousSamples = sample(frame, homography);

		return frameFromHomography(homography);
	}

	private ObjectFrame frameFromHomography(final SimpleMatrix homography) {
		return new ObjectFrame(
				warpPoint(homography, initialFrame.getFirst()),
				warpPoint(homography, initialFrame.getSecond()),
				warpPoint(homography, initialFrame.getThird()),
				warpPoint(homography, initialFrame.getFourth())
				);
	}

	private SimpleMatrix homography(final DenseMatrix64F deltaMatrix) {
		SimpleMatrix result = new SimpleMatrix(3, 3);
		for (int i = 0; i < 8; i++) {
			result.set(i, deltaMatrix.get(i));
		}
		result.set(8, 1);

		return result;
	}

	private double[] sample(final BufferedImage frame, final SimpleMatrix homography) {

		double[] samples = new double[subsets.size()];
		for (int i = 0; i < samples.length; i++) {
			Point result = warpPoint(homography, subsets.get(i));

			int x = result.x;
			int y = result.y;
			while (x < 0) {
				x += PIXELS_PER_SAMPLE;
			}
			while (x >= frame.getWidth()) {
				x -= PIXELS_PER_SAMPLE;
			}

			while (y < 0) {
				y += PIXELS_PER_SAMPLE;
			}
			while (y >= frame.getHeight()){
				y -= PIXELS_PER_SAMPLE;
			}

			Color color = new Color(frame.getRGB(x, y));
			samples[i] = (color.getBlue() + color.getGreen() + color.getRed()) / 3;
		}

		return samples;
	}

	private Point warpPoint(final SimpleMatrix homography, final Point point) {

		DenseMatrix64F pointMatrix = new DenseMatrix64F(3, 1);
		DenseMatrix64F warpedPoint = new DenseMatrix64F(3, 1);

		pointMatrix.set(0, point.x - initialFrame.getCenterX());
		pointMatrix.set(1, point.y - initialFrame.getCenterY());
		pointMatrix.set(2, 1);

		CommonOps.mult(homography.getMatrix(), pointMatrix, warpedPoint);
		int x = (int) (warpedPoint.get(0) / warpedPoint.get(2)) + initialFrame.getCenterX();
		int y = (int) (warpedPoint.get(1) / warpedPoint.get(2)) + initialFrame.getCenterY();

		return new Point(x, y);
	}

	private void trainPredictors(final BufferedImage frame) {

		subsets = subsetsFromFrame(initialFrame);
		homography = SimpleMatrix.identity(3);
		double[] referenceSamples = sample(frame, homography);

		System.out.println(referenceSamples.length);
		int numberOfSamples = referenceSamples.length;
		int mutationCount = numberOfSamples * 40;

		double[][] samplesDelta = new double[mutationCount][];
		double[][] templateDelta = new double[mutationCount][];

		for (int i = 0; i < mutationCount; i++) {
			SimpleMatrix mutatedHomography;
			Point warpPoint;

			do {
				mutatedHomography = randomHomography();
				warpPoint = warpPoint(mutatedHomography, new Point(100, 100));
			} while (Math.abs(warpPoint.x - 100) > 15 || Math.abs(warpPoint.y - 100) > 15);

			samplesDelta[i] = sub(sample(frame, mutatedHomography), referenceSamples);
			templateDelta[i] = extractParams(mutatedHomography);
		}

		//template = null;

		SimpleMatrix transposedH = new SimpleMatrix(samplesDelta);
		SimpleMatrix h = transposedH.transpose();
		SimpleMatrix y = new SimpleMatrix(templateDelta).transpose();

		//samplesDelta = null;
		//templateDelta = null;

		SimpleMatrix inverse = h.mult(transposedH).invert();
		h = null;

		predictor = y.mult(transposedH.mult(inverse)).getMatrix();
		previousSamples = referenceSamples;

		DenseMatrix64F calculatedDelta = new DenseMatrix64F(8, 1);
		double acummulatedError = 0;
		double maxError = Double.MIN_VALUE;
		for (int i = 0; i < mutationCount; i++) {

			DenseMatrix64F deltaMatrix = new DenseMatrix64F(samplesDelta[i].length, 1, true, samplesDelta[i]);
			CommonOps.mult(predictor, deltaMatrix, calculatedDelta);

			double err = 0;
			for (int j = 0; j < 8; j++) {
				err += Math.pow(calculatedDelta.data[j] - templateDelta[i][j], 2);
			}

			err = Math.sqrt(err);
			if (maxError < err) {
				maxError = err;
			}
			acummulatedError += err;
		}

		System.out.println("error average: " + acummulatedError / mutationCount);
		System.out.println("max error: " + maxError);

		System.out.println("happiness");
	}

	private SimpleMatrix randomHomography() {
		int dx = random.nextInt(25) - 12;
		int dy = random.nextInt(25) - 12;

		double s = random.nextDouble() * 0.4 + 0.6;
		double angle = random.nextDouble() * Math.PI / 6;

		//TODO: Incorporate skweing
		SimpleMatrix mutatedHomography = new SimpleMatrix(3, 3);
		mutatedHomography.set(0, 0, s * Math.cos(angle));
		mutatedHomography.set(0, 1, -s * Math.sin(angle));
		mutatedHomography.set(0, 2, dx);
		mutatedHomography.set(1, 0, s * Math.sin(angle));
		mutatedHomography.set(1, 1, s * Math.cos(angle));
		mutatedHomography.set(1, 2, dy);
		mutatedHomography.set(2, 0, 0 /* p0 */);
		mutatedHomography.set(2, 1, 0 /* p1 */);
		mutatedHomography.set(2, 2, 1);
		return mutatedHomography;
	}

	private double[] extractParams(final SimpleMatrix homography) {
		return Arrays.copyOf(homography.getMatrix().data, 8);
	}

	private double[] sub(final double[] a, final double[] b) {

		double[] res = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] - b[i];
		}

		return res;
	}

}
