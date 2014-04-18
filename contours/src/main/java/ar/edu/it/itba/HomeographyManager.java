/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.simple.SimpleMatrix;

import ar.edu.it.itba.processing.Homography;

/**
 *
 * @author eordano
 */
public class HomeographyManager {

    private final List<Pair> points = new ArrayList<Pair>();
    private ListModel<Pair> listModel;

    public HomeographyManager() {
        listModel = new AbstractListModel<Pair>() {

			private static final long serialVersionUID = 4973707810214568136L;

			@Override
            public int getSize() {
                return points.size();
            }

            @Override
            public Pair getElementAt(final int index) {
                return points.get(index);
            }
        };
    }

    void setMapping(final Point imagePoint, final Point mappedPoint) {
        points.add(new Pair(imagePoint, mappedPoint));
    }

    public Homography calculateHomography() {
    	if (points.size() < 4) {
    		return null;
    	}

    	DenseMatrix64F a = new DenseMatrix64F(points.size() * 2, 9);

    	for (int i = 0; i < points.size(); i++) {
    		Pair pair = points.get(i);
    		a.set(i * 2, 0, -pair.image.x);
    		a.set(i * 2, 1, -pair.image.y);
    		a.set(i * 2, 2, -1);

    		a.set(i * 2, 6, pair.image.x * pair.mapped.x);
    		a.set(i * 2, 7, pair.image.y * pair.mapped.x);
    		a.set(i * 2, 8, pair.mapped.x);

    		a.set(i * 2 + 1, 3, -pair.image.x);
    		a.set(i * 2 + 1, 4, -pair.image.y);
    		a.set(i * 2 + 1, 5, -1);

    		a.set(i * 2 + 1, 6, pair.image.x * pair.mapped.y);
    		a.set(i * 2 + 1, 7, pair.image.y * pair.mapped.y);
    		a.set(i * 2 + 1, 8, pair.mapped.y);
    	}

		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(points.size() * 2, 9, false, true, false);
		svd.decompose(a);
		DenseMatrix64F v = svd.getV(null, false);
		SimpleMatrix result = new SimpleMatrix(3, 3);

		// Remember to normalize the laste element to 1
		double ratio = 1.0 / v.get(8, v.numCols - 1);
		for (int i = 0; i < 9; i++) {
			result.set(i, ratio * v.get(i, v.numCols - 1));
		}
		Homography h = new Homography(result);
		double errsum = 0;
		for (Pair p : points) {
			Point d = h.apply(p.image);
			errsum += Math.pow(Math.abs(d.x - p.mapped.x), 2) + Math.pow(Math.abs(d.y - p.mapped.y), 2);
		}
		System.out.println("Error en homography: " + (errsum / points.size()));

		return h;
    }

    ListModel<Pair> getListModel() {
        return listModel;
    }

    void removeItem(final int selectedIndex) {
        points.remove(selectedIndex);
    }

    public List<Pair> getPairs() {
        return new ArrayList<Pair>(points);
    }

    public static class Pair {

        public final Point image;
        public final Point mapped;
        public Pair(final Point a, final Point b) {
            image = a;
            mapped = b;
        }
        @Override
        public String toString() {
            return String.format("(%s, %s) -> (%s, %s)", image.x, image.y, mapped.x, mapped.y);
        }
    }

	public Homography calculateIterativeHomegraphy(final int n) {
		Homography currentH = calculateHomography();
		for (int i = 0; i < n; i++) {
			Homography h_inverse = currentH.getInverse();
			List<Point> proyectedMappedPoints = new LinkedList<Point>();
			for (Pair pair : points) {
				proyectedMappedPoints.add(h_inverse.apply(pair.mapped));
			}
			HomeographyManager auxManager = new HomeographyManager();
			for (int j = 0; j < points.size(); j++) {
				auxManager.setMapping(proyectedMappedPoints.get(j), points.get(j).image);
			}
			Homography h_1 = auxManager.calculateHomography();
			currentH = currentH.compose(h_1);
		}
		return currentH;
	}

}
