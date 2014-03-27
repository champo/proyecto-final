package ar.edu.it.itba.video;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class BlackOutOutskirts implements FrameProvider {

	private final FrameProvider provider;
	private final List<Point> points;
	private boolean[][] isInside;
	
	private int minX = Integer.MAX_VALUE;
	private int minY = Integer.MAX_VALUE;
	private int maxX = 0;
	private int maxY = 0;
	private int width, height;

	public BlackOutOutskirts(FrameProvider provider, List<Point> points) {
		super();
		this.provider = provider;
		this.points = new ArrayList<Point>(points);
	}

	@Override
	public BufferedImage nextFrame() {
		BufferedImage frame = provider.nextFrame();
		if (isInside == null) {
			isInside = new boolean[frame.getWidth()][frame.getHeight()];
			calculateInsidePoints(frame);
		}
		return blackoutOutskirts(frame);
	}

	private void calculateInsidePoints(BufferedImage frame) {
		boolean[][] borders = new boolean[frame.getWidth()][frame.getHeight()];
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

	private BufferedImage blackoutOutskirts(BufferedImage frame) {
		BufferedImage cropped = new BufferedImage(width, height, frame.getType());
		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j < maxY; j++) {
				if (!isInside[i][j]) {
					cropped.setRGB(i-minX, j-minY, Color.BLACK.getRGB());
				} else {
					cropped.setRGB(i-minX, j-minY, frame.getRGB(i, j));
				}
			}
		}
		return cropped;
	}
}
