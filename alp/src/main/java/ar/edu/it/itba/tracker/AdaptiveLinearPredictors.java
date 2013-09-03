package ar.edu.it.itba.tracker;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
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

	private ObjectFrame previousFrame;

	private double[] previousSamples;

	private DenseMatrix64F predictor;

	private List<Point> subsets;

	private static Random random = new Random();

	public AdaptiveLinearPredictors(final ObjectFrame initialFrame) {
		super();
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

		double[] template = templateFromObjectFrame(previousFrame);

		TemplateMatrix homography = new TemplateMatrix(homography(template));

		DenseMatrix64F templateDelta = new DenseMatrix64F(8, 1);

		for (int i = 0; i < 3; i++) {

			// Estas estan MAAAAAAAAAAAAAAAAAAAAAL
			// Idea, cablear los valores reales para testear con el primer frame
			int dx = getMinX(homography.points) - previousFrame.getMinX();
			int dy = getMinY(homography.points) - previousFrame.getMinY();

			double[] samples = trainingSample(frame, subsets, dx, dy);
			double[] sampleDelta = sub(samples, previousSamples);

			DenseMatrix64F deltaMatrix = new DenseMatrix64F(sampleDelta.length, 1, true, sampleDelta);
			CommonOps.mult(predictor, deltaMatrix, templateDelta);
			homography = homography.multiplyBy(new TemplateMatrix(templateDelta.data));
		}

		double[] resultingTemplate = templateFromHomography(homography.points);
		// TODO: Check error rate by checking against reference samples

		double[] templateDiff = sub(resultingTemplate, template);

		for (Point samplePoint : subsets) {
			samplePoint.translate((int) templateDiff[0], (int) templateDiff[1]);
		}
		previousSamples = trainingSample(frame, subsets, 0, 0);

		previousFrame = new ObjectFrame(
			new Point((int) resultingTemplate[0], (int) resultingTemplate[1]),
			new Point((int) resultingTemplate[2], (int) resultingTemplate[3]),
			new Point((int) resultingTemplate[4], (int) resultingTemplate[5]),
			new Point((int) resultingTemplate[6], (int) resultingTemplate[7])
		);

		return previousFrame;
	}

	private int getMinX(final double[] points) {
		double min = Double.MAX_VALUE;
		for (int i = 0; i < points.length; i += 2) {
			if (min > points[i]) {
				min = points[i];
			}
		}

		return (int) min;
	}

	private int getMinY(final double[] points) {
		double min = Double.MAX_VALUE;
		for (int i = 1; i < points.length; i += 2) {
			if (min > points[i]) {
				min = points[i];
			}
		}

		return (int) min;
	}
	private double[] homography(final double[] template) {
		// ???????
		return template;
	}

	private double[] templateFromHomography(final double[] homography) {
		return homography;
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
			samples[i] = color.getBlue() + color.getGreen() + color.getRed();
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
