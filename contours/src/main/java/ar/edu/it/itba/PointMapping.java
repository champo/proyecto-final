package ar.edu.it.itba;

import java.awt.Point;
import java.util.Map;


public class PointMapping {

	private Map<Point, Double> map;

	public PointMapping(Map<Point, Double> map) {
		this.map = map;
	}
	
	public double getValue(Point p) {
		Double b = map.get(p);
		if (b == null) {
			return 0;
		}
		return b;
	}
	public double getValue(int x, int y) {
		return map.get(new Point(x, y));
	}

	public void set(Point p, double i) {
		map.put(p, i);
	}
}
