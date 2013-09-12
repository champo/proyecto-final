package ar.edu.it.itba.tracker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class ObjectFrame {

	private final Point first;

	private final Point second;

	private final Point third;

	private final Point fourth;

	public ObjectFrame(final Point first, final Point second, final Point third,
			final Point fourth) {
		super();
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
	}

	public Point getFirst() {
		return first;
	}

	public Point getSecond() {
		return second;
	}

	public Point getThird() {
		return third;
	}

	public Point getFourth() {
		return fourth;
	}

	public int getMinX() {
		int min = Integer.MAX_VALUE;
		if (min > first.x) {
			min = first.x;
		}

		if (min > second.x) {
			min = second.x;
		}

		if (min > third.x) {
			min = third.x;
		}

		if (min > fourth.x) {
			min = fourth.x;
		}

		return min;
	}

	public int getMinY() {
		int min = Integer.MAX_VALUE;
		if (min > first.y) {
			min = first.y;
		}

		if (min > second.y) {
			min = second.y;
		}

		if (min > third.y) {
			min = third.y;
		}

		if (min > fourth.y) {
			min = fourth.y;
		}

		return min;
	}

	public int getMaxX() {
		int max = Integer.MIN_VALUE;
		if (max < first.x) {
			max = first.x;
		}

		if (max < second.x) {
			max = second.x;
		}

		if (max < third.x) {
			max = third.x;
		}

		if (max < fourth.x) {
			max = fourth.x;
		}

		return max;
	}

	public int getMaxY() {
		int max = Integer.MIN_VALUE;
		if (max < first.y) {
			max = first.y;
		}

		if (max < second.y) {
			max = second.y;
		}

		if (max < third.y) {
			max = third.y;
		}

		if (max < fourth.y) {
			max = fourth.y;
		}

		return max;
	}

	public void drawOnImage(final BufferedImage image) {

		Graphics graphics = image.getGraphics();

		graphics.setColor(Color.red);

		graphics.drawLine(first.x, first.y, third.x, third.y);
		graphics.drawLine(third.x, third.y, fourth.x, fourth.y);
		graphics.drawLine(fourth.x, fourth.y, second.x, second.y);
		graphics.drawLine(second.x, second.y, first.x, first.y);

	}

	public int getCenterY() {
		return (getMaxY() - getMinY()) / 2;
	}

	public int getCenterX() {
		return (getMaxX() - getMinX()) / 2;
	}
}
