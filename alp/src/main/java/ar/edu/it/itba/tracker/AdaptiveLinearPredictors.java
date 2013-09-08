package ar.edu.it.itba.tracker;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
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

	private ObjectFrame previousFrame;

	private final ObjectFrame initialFrame;

	private double[] previousSamples;

	private DenseMatrix64F predictor;

	private List<Point> subsets;

	private static Random random = new Random();

	public AdaptiveLinearPredictors(final ObjectFrame initialFrame) {
		super();
		this.initialFrame = initialFrame;
		this.previousFrame = initialFrame;
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

			return previousFrame;
		}

		SimpleMatrix homography = homographyFromFrame(previousFrame);
		DenseMatrix64F templateDelta = new DenseMatrix64F(8, 1);

		for (int i = 0; i < 3; i++) {

			double[] samples = sample(frame, homography);
			double[] sampleDelta = sub(samples, previousSamples);

			DenseMatrix64F deltaMatrix = new DenseMatrix64F(sampleDelta.length, 1, true, sampleDelta);
			CommonOps.mult(predictor, deltaMatrix, templateDelta);
			homography = homography.mult(homography(templateDelta));
		}

		previousFrame = updateFrameWithHomography(homography);
		// TODO: Check error rate by checking against reference samples

		previousSamples = sample(frame, homography);

		return previousFrame;
	}

	private SimpleMatrix homographyFromFrame(final ObjectFrame frame) {
		return homography(new DenseMatrix64F(8, 1, true, templateFromObjectFrame(frame)));
	}

	private ObjectFrame updateFrameWithHomography(final SimpleMatrix homography) {
		return new ObjectFrame(
				warpPoint(homography, initialFrame.getFirst()),
				warpPoint(homography, initialFrame.getSecond()),
				warpPoint(homography, initialFrame.getThird()),
				warpPoint(homography, initialFrame.getFourth())
				);
	}

	private SimpleMatrix homography(final DenseMatrix64F deltaMatrix) {

		DenseMatrix64F a = new DenseMatrix64F(8, 9);

		// First pair of points
		a.set(0, 0, -initialFrame.getFirst().x);
		a.set(0, 1, -initialFrame.getFirst().y);
		a.set(0, 2, -1);

		a.set(0, 6, initialFrame.getFirst().x * deltaMatrix.get(0));
		a.set(0, 7, initialFrame.getFirst().y * deltaMatrix.get(0));
		a.set(0, 8, deltaMatrix.get(0));

		a.set(1, 3, -initialFrame.getFirst().x);
		a.set(1, 4, -initialFrame.getFirst().y);
		a.set(1, 5, -1);

		a.set(1, 6, initialFrame.getFirst().x * deltaMatrix.get(1));
		a.set(1, 7, initialFrame.getFirst().y * deltaMatrix.get(1));
		a.set(1, 8, deltaMatrix.get(1));

		// Second pair of points

		a.set(2, 0, -initialFrame.getSecond().x);
		a.set(2, 1, -initialFrame.getSecond().y);
		a.set(2, 2, -1);

		a.set(2, 6, initialFrame.getSecond().x * deltaMatrix.get(2));
		a.set(2, 7, initialFrame.getSecond().y * deltaMatrix.get(2));
		a.set(2, 8, deltaMatrix.get(2));

		a.set(3, 3, -initialFrame.getSecond().x);
		a.set(3, 4, -initialFrame.getSecond().y);
		a.set(3, 5, -1);

		a.set(3, 6, initialFrame.getSecond().x * deltaMatrix.get(3));
		a.set(3, 7, initialFrame.getSecond().y * deltaMatrix.get(3));
		a.set(3, 8, deltaMatrix.get(3));

		// Third pair of points

		a.set(4, 0, -initialFrame.getThird().x);
		a.set(4, 1, -initialFrame.getThird().y);
		a.set(4, 2, -1);

		a.set(4, 6, initialFrame.getThird().x * deltaMatrix.get(4));
		a.set(4, 7, initialFrame.getThird().y * deltaMatrix.get(4));
		a.set(4, 8, deltaMatrix.get(4));

		a.set(5, 3, -initialFrame.getThird().x);
		a.set(5, 4, -initialFrame.getThird().y);
		a.set(5, 5, -1);

		a.set(5, 6, initialFrame.getThird().x * deltaMatrix.get(5));
		a.set(5, 7, initialFrame.getThird().y * deltaMatrix.get(5));
		a.set(5, 8, deltaMatrix.get(5));

		// Fourth pair of points

		a.set(6, 0, -initialFrame.getFourth().x);
		a.set(6, 1, -initialFrame.getFourth().y);
		a.set(6, 2, -1);

		a.set(6, 6, initialFrame.getFourth().x * deltaMatrix.get(6));
		a.set(6, 7, initialFrame.getFourth().y * deltaMatrix.get(6));
		a.set(6, 8, deltaMatrix.get(6));

		a.set(7, 3, -initialFrame.getFourth().x);
		a.set(7, 4, -initialFrame.getFourth().y);
		a.set(7, 5, -1);

		a.set(7, 6, initialFrame.getFourth().x * deltaMatrix.get(7));
		a.set(7, 7, initialFrame.getFourth().y * deltaMatrix.get(7));
		a.set(7, 8, deltaMatrix.get(7));

		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(8, 9, false, true, false);
		svd.decompose(a);
		DenseMatrix64F v = svd.getV(null, false);
		SimpleMatrix result = new SimpleMatrix(3, 3);

		// Remember to normalize the laste element to 1
		double ratio = 1.0 / v.get(8, v.numCols - 1);
		for (int i = 0; i < 9; i++) {
			result.set(i, ratio * v.get(i, v.numCols - 1));
		}

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

		pointMatrix.set(0, point.x);
		pointMatrix.set(1, point.y);
		pointMatrix.set(2, 1);

		CommonOps.mult(homography.getMatrix(), pointMatrix, warpedPoint);
		int x = (int) (warpedPoint.get(0) / warpedPoint.get(2));
		int y = (int) (warpedPoint.get(1) / warpedPoint.get(2));

		return new Point(x, y);
	}

	private void trainPredictors(final BufferedImage frame) {

		subsets = subsetsFromFrame(previousFrame);
		double[] referenceSamples = trainingSample(frame, subsets, 0, 0);
		double[] template = templateFromObjectFrame(previousFrame);

		System.out.println(referenceSamples.length);
		int numberOfSamples = referenceSamples.length;
		int mutationCount = numberOfSamples * 40;

		double[][] samplesDelta = new double[mutationCount][];
		double[][] templateDelta = new double[mutationCount][];

		for (int i = 0; i < mutationCount; i++) {
			int dx = random.nextInt(25) - 12;
			int dy = random.nextInt(25) - 12;

			samplesDelta[i] = sub(trainingSample(frame, subsets, dx, dy), referenceSamples);
			templateDelta[i] = sub(templateFromObjectFrame(mutate(previousFrame, dx, dy)), template);
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

		int dx = random.nextInt(25) - 12;
		int dy = random.nextInt(25) - 12;

		double[] randomDelta = sub(trainingSample(frame, subsets, dx, dy), referenceSamples);
		double[] randomTemplateDelta = sub(templateFromObjectFrame(mutate(previousFrame, dx, dy)), template);

		DenseMatrix64F deltaMatrix = new DenseMatrix64F(randomDelta.length, 1, true, randomDelta);
		CommonOps.mult(predictor, deltaMatrix, calculatedDelta);

		double err = 0;
		for (int j = 0; j < 8; j++) {
			err += Math.pow(calculatedDelta.data[j] - randomTemplateDelta[j], 2);
		}
		System.out.println("fake error: " + Math.sqrt(err));

		System.out.println("happiness");
	}

	private double[] sub(final double[] a, final double[] b) {

		double[] res = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			res[i] = a[i] - b[i];
		}

		return res;
	}

	private double[] trainingSample(final BufferedImage frame, final List<Point> subsets, final int dx, final int dy) {

		double[] samples = new double[subsets.size()];
		for (int i = 0; i < samples.length; i++) {
			Point point = subsets.get(i);

			int x = point.x + dx;
			while (x < 0) {
				x += PIXELS_PER_SAMPLE;
			}
			while (x >= frame.getWidth()) {
				x -= PIXELS_PER_SAMPLE;
			}

			int y = point.y + dy;
			while (y < 0) {
				y += PIXELS_PER_SAMPLE;
			}
			while (y >= frame.getHeight()){
				y -= PIXELS_PER_SAMPLE;
			}

			Color color = new Color(frame.getRGB(x, y));
			samples[i] = (color.getBlue() + color.getGreen() + color.getRed() ) / 3;
		}

		return samples;
	}

	private double[] templateFromObjectFrame(final ObjectFrame frame) {
		return new double[] {
			frame.getFirst().x,
			frame.getFirst().y,
			frame.getSecond().x,
			frame.getSecond().y,
			frame.getThird().x,
			frame.getThird().y,
			frame.getFourth().x,
			frame.getFourth().y
		};
	}

	private ObjectFrame mutate(final ObjectFrame original, final int dx, final int dy) {

		ObjectFrame res = new ObjectFrame(
			new Point(original.getFirst()),
			new Point(original.getSecond()),
			new Point(original.getThird()),
			new Point(original.getFourth())
		);

		res.getFirst().translate(dx, dy);
		res.getSecond().translate(dx, dy);
		res.getThird().translate(dx, dy);
		res.getFourth().translate(dx, dy);

		return res;
	}

}
