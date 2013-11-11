package ar.edu.it.itba.processing;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;


public class PointMapping {

	private final Map<Point, Double> map;

	private Provider provider;

	public static interface Provider {

		public double valueForPoint(Point p);

	}

	public PointMapping(final Provider provider) {
		this.map = new HashMap<Point, Double>();
		this.provider = provider;
	}

	public void setProvider(final Provider provider) {
		this.provider = provider;
	}

	public double getValue(final Point p) {
		Double b = map.get(p);
		if (b == null) {
			double value = provider.valueForPoint(p);
			map.put(p, value);
			return value;
		}
		return b;
	}
	public double getValue(final int x, final int y) {
		return getValue(new Point(x, y));
	}

	public void set(final Point p, final double i) {
		map.put(p, i);
	}
}
