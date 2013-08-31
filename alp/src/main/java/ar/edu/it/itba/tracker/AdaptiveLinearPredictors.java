package ar.edu.it.itba.tracker;

import java.awt.Point;
import java.awt.image.BufferedImage;

public class AdaptiveLinearPredictors {

	public TrackResult trackFrame(final BufferedImage frame) {
		return new TrackResult(new Point(10, 10), new Point(10, 100), new Point(100, 10), new Point(100, 100));
	}

}
