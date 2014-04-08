package ar.edu.it.itba.video;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BlackOutOutskirts extends AbstractFrameProviderDecorator {

	private final List<Point> points;
	private boolean[][] isInside;
	
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxX = 0;
	private int maxY = 0;
	private int width, height;

	public BlackOutOutskirts(FrameProvider provider, List<Point> points) {
		super(provider);
		this.points = new ArrayList<Point>(points);
	}

	@Override
	public void nextFrame() {
		provider.nextFrame();
		if (isInside == null) {
			isInside = new boolean[provider.getWidth()][provider.getHeight()];
			calculateInsidePoints();
		}
	}

	private void calculateInsidePoints() {
		boolean[][] borders = new boolean[provider.getWidth()][provider.getHeight()];
		Point prev = null;
		points.add(points.get(0));
		for (Point p : points) {
			if (prev != null) {
				int delta = Math.abs(p.x - prev.x) + Math.abs(p.y - prev.y);
				double deltaX = p.x - prev.x;
				double deltaY = p.y - prev.y;
				for (int k = 0; k < delta; k++) {
					borders[(int) (prev.x + deltaX * k / delta)][(int) (prev.y + deltaY * k / delta)] = true;
				}
			}
			prev = p;
		}
		for (int i = 0; i < isInside.length; i++) {
			int border = 0;
			boolean continued = false;
			int lastChange = 0;
			for (int j = 0; j < isInside[i].length; j++) {
				isInside[i][j] = (border & 1) != 0;
				if (borders[i][j]) {
					if (!continued) {
						border++;
						continued = true;
						lastChange = j;
					}
				} else {
					continued = false;
				}
			}
			if ((border & 1) != 0) {
				for (int j = lastChange + 1; j < isInside[i].length; j++) {
					isInside[i][j] = false;
				}
			}
		}

		for (int i = 0; i < isInside.length; i++) {
			for (int j = 0; j < isInside[i].length; j++) {
				if (isInside[i][j]) {
					minY = Math.min(minY, j);
					maxY = Math.max(maxY, j);
					minX = Math.min(minX, i);
					maxX = Math.max(maxX, i);
				}
			}
		}
		width = maxX - minX + 1;
		height = maxY - minY + 1;
		System.out.println(width + " - " + height);
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getRGB(int x, int y) {
		if (isInside[x + minX][y + minY]) {
			return provider.getRGB(x + minX, y + minY);
		}
		return Color.black.getRGB();
	}
}
