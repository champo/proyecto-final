/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author eordano
 */
class HomeographyManager {

    private final List<Pair> points = new ArrayList<Pair>();
    private ListModel listModel;

    public HomeographyManager() {
    	points.add(new Pair(new Point(183, 108), new Point(46, 124)));
    	points.add(new Pair(new Point(86, 160), new Point(45, 179)));
    	points.add(new Pair(new Point(219, 228), new Point(77, 208)));
    	points.add(new Pair(new Point(324, 93), new Point(77, 92)));
    	points.add(new Pair(new Point(213, 75), new Point(31, 23)));
        listModel = new AbstractListModel() {

            @Override
            public int getSize() {
                return points.size();
            }

            @Override
            public Object getElementAt(final int index) {
                return points.get(index);
            }
        };
    }

    void setMapping(final Point imagePoint, final Point mappedPoint) {
        points.add(new Pair(imagePoint, mappedPoint));
    }

    public SimpleMatrix calculateHomography() {
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

		return result;
    }

    ListModel getListModel() {
        return listModel;
    }

    void removeItem(final int selectedIndex) {
        points.remove(selectedIndex);
    }

    private static class Pair {

        private final Point image;
        private final Point mapped;
        public Pair(final Point a, final Point b) {
            image = a;
            mapped = b;
        }
        @Override
        public String toString() {
            return String.format("(%s, %s) -> (%s, %s)", image.x, image.y, mapped.x, mapped.y);
        }
    }

}
