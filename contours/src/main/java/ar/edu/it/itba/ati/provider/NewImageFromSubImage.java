package ar.edu.it.itba.ati.provider;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.Operations;


public class NewImageFromSubImage extends CanvasProvider {

	private final Canvas canvas;

	public NewImageFromSubImage(final Operations origin) {
		canvas = origin.calculateCanvas();
	}

	@Override
	public Canvas getCanvas() {
		return new Canvas(canvas);
	}
}
