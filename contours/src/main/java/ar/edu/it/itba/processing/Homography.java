package ar.edu.it.itba.processing;

import java.awt.Point;

import org.ejml.simple.SimpleMatrix;

public class Homography {

	final SimpleMatrix mat;
	private SimpleMatrix inverse;

	public Homography(final SimpleMatrix mat) {
		this.mat = mat;
	}

	public Point apply(final Point p) {
		return apply(p.x, p.y);
	}

	public Point apply(final int x, final int y) {
		SimpleMatrix pos = new SimpleMatrix(3, 1);
		pos.set(0, x);
		pos.set(1, y);
		pos.set(2, 1);

		SimpleMatrix mapped = mat.mult(pos);
		mapped = mapped.divide(mapped.get(2));

		return new Point((int) mapped.get(0), (int) mapped.get(1));
	}

	public Point inverseApply(final int x, final int y) {
		SimpleMatrix pos = new SimpleMatrix(3, 1);
		pos.set(0, x);
		pos.set(1, y);
		pos.set(2, 1);

		SimpleMatrix mapped = getInverseMatrix().mult(pos);
		mapped = mapped.divide(mapped.get(2));
		return new Point((int) mapped.get(0), (int) mapped.get(1));
	}

    public Point inverseApply(Double fieldWidth, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Homography getInverse() {
        return new Homography(getInverseMatrix());
    }

	private SimpleMatrix getInverseMatrix() {
		if (this.inverse == null) {
			this.inverse = mat.invert();
		}
		return this.inverse;
	}

    public Homography compose(Homography otherHomography) {
        return new Homography(mat.mult(otherHomography.mat));
    }
}
