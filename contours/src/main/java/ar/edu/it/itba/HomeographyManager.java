/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;

/**
 *
 * @author eordano
 */
class HomeographyManager {
    
    private List<Pair> points = new ArrayList<>();
    private ListModel listModel;
    
    public HomeographyManager() {
        listModel = new AbstractListModel() {

            @Override
            public int getSize() {
                return points.size();
            }

            @Override
            public Object getElementAt(int index) {
                return points.get(index);
            }
        };
    }

    void setMapping(Point imagePoint, Point mappedPoint) {
        points.add(new Pair(imagePoint, mappedPoint));
    }

    ListModel getListModel() {
        return listModel;
    }

    void removeItem(int selectedIndex) {
        points.remove(selectedIndex);
    }

    private static class Pair {

        private Point image;
        private Point mapped;
        public Pair(Point a, Point b) {
            image = a;
            mapped = b;
        }
        @Override
        public String toString() {
            return String.format("(%s, %s) -> (%s, %s)", image.x, image.y, mapped.x, mapped.y);
        }
    }
    
}
