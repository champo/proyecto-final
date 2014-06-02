package ar.edu.it.itba.ati.operation.tp1;


import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class MultiplyImage extends Operation {

	final Canvas c;
	public MultiplyImage(final Canvas c) {
		this.c = c;
	}

	@Override
	public void operate(final Canvas original) {
		for (int i = 0; i < original.getWidth() * original.getHeight(); i++) {
			original.red[i] *= c.red[i];
			original.green[i] *= c.green[i];
			original.blue[i] *= c.blue[i];
		}

		new LinearNormalization().operate(original);
	}

}
