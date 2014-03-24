package ar.edu.it.itba.processing;


public class HSPoint extends HSIPoint {

	public HSPoint(final int red, final int green, final int blue) {
		super(red, green, blue);
		this.blue = 0;
	}

	public HSPoint(final int rgb) {
		super(rgb);
		this.blue = 0;
	}


	@Override
	public Type getType() {
		return Type.HS;
	}
}
