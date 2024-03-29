package ar.edu.it.itba.video;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.tp1.Contrast;
import ar.edu.it.itba.ati.operation.tp3.TebexHough;
import ar.edu.it.itba.ati.provider.ImageFromBuffer;

public class HoughLines extends AbstractFrameProviderDecorator {

	private boolean[][] isLine;

	public HoughLines(FrameProvider provider, BufferedImage frame) {
		super(provider);
		isLine = new boolean[provider.getWidth()][provider.getHeight()];
		calculateHough(frame);
	}

	private void calculateHough(BufferedImage frame) {
		Canvas canvas = new ImageFromBuffer(frame).getCanvas();
		Canvas original = new ImageFromBuffer(frame).getCanvas();
		new Contrast(127, 173, 56, 200).operate(canvas);
		new TebexHough().operate(canvas);
		for (int i = 0; i < canvas.width; i++) {
			for (int j = 0; j < canvas.height; j++) {
				isLine[i][j] = canvas.getGrey(i, j) == 255 && original.calculateGrey(i, j) != 0;
			}
		}
	}

	@Override
	public int getRGB(int x, int y) {
		if (isLine[x][y]) {
			return Color.black.getRGB();
		}
		return provider.getRGB(x, y);
	}

	public void clearAround(Point lastCentroid) {
		for (int i = -10; i < 10; i++) {
			if (lastCentroid.x + i >= 0 && lastCentroid.x + i < getWidth()) {
				for (int j = -20; j < 20; j++) {
					if (lastCentroid.y + j >= 0 && lastCentroid.y + j < getHeight()) {
						isLine[lastCentroid.x + i][lastCentroid.y + j] = false;
					}
				}
			}
		}
	}
}
