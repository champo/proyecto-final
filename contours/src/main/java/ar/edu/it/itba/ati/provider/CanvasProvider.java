package ar.edu.it.itba.ati.provider;

import ar.edu.it.itba.ati.Canvas;

public abstract class CanvasProvider implements Cloneable {

	public abstract Canvas getCanvas();

	@Override
	public CanvasProvider clone() throws CloneNotSupportedException {
		return (CanvasProvider) super.clone();
	}

}
