package ar.edu.it.itba.tracker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

public class TrackResult {

	private final Point upperLeft;

	private final Point lowerLeft;

	private final Point upperRight;

	private final Point lowerRight;

	public TrackResult(final Point upperLeft, final Point lowerLeft, final Point upperRight,
			final Point lowerRight) {
		super();
		this.upperLeft = upperLeft;
		this.lowerLeft = lowerLeft;
		this.upperRight = upperRight;
		this.lowerRight = lowerRight;
	}

	public Point getUpperLeft() {
		return upperLeft;
	}

	public Point getLowerLeft() {
		return lowerLeft;
	}

	public Point getUpperRight() {
		return upperRight;
	}

	public Point getLowerRight() {
		return lowerRight;
	}

	public void drawOnImage(final BufferedImage image) {

		Graphics graphics = image.getGraphics();

		graphics.setColor(Color.red);

		graphics.drawLine(upperLeft.x, upperLeft.y, upperRight.x, upperRight.y);
		graphics.drawLine(upperRight.x, upperRight.y, lowerRight.x, lowerRight.y);
		graphics.drawLine(lowerRight.x, lowerRight.y, lowerLeft.x, lowerLeft.y);
		graphics.drawLine(lowerLeft.x, lowerLeft.y, upperLeft.x, upperLeft.y);

	}

}
