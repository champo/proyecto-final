package ar.edu.it.itba.tracker;

import java.util.Arrays;

public class TemplateMatrix {

	double[] points;

	public TemplateMatrix(final double[] points) {
		super();
		this.points = Arrays.copyOf(points, points.length);
	}

	public TemplateMatrix multiplyBy(final TemplateMatrix matrix) {
		double[] res = new double[points.length];

		if (true) {
			for (int i = 0; i < 8; i++) {
				res[i] = points[i] + matrix.points[i];
			}
		}

		// translation a_i,j = (points[4 * i + 2 * j], points[4 * i + 2 * j + 1])
		// c_0,0 = a_0,0 * b_0,0 + a_0,1 * b_1,0
		res[0] = points[0] * matrix.points[0] + points[2] * matrix.points[4];
		res[1] = points[1] * matrix.points[1] + points[3] * matrix.points[5];

		// c_1,0 = a_1,0 * b_0,0 + a_1,1 * b_1,0
		res[2] = points[4] * matrix.points[0] + points[6] * matrix.points[4];
		res[3] = points[5] * matrix.points[1] + points[7] * matrix.points[5];

		// c_0,1 = a_0,0 * b_0,1 + a_0,1 * b_1,1
		res[4] = points[0] * matrix.points[2] + points[2] * matrix.points[6];
		res[5] = points[1] * matrix.points[3] + points[3] * matrix.points[7];

		// c_1,1 = a_1,0 * b_0,1 + a_1,1 * b_1,1
		res[6] = points[4] * matrix.points[2] + points[6] * matrix.points[6];
		res[7] = points[5] * matrix.points[3] + points[7] * matrix.points[7];


		return new TemplateMatrix(res);

	}

}
