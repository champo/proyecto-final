package ar.edu.it.itba.ati.operation.tp2.laplace;

import java.util.Arrays;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class ZeroCrossFilter extends Operation {

	@Override
	public void operate(final Canvas original) {
		operateOnLayer(original.red, original.getWidth(), original.getHeight());
		operateOnLayer(original.green, original.getWidth(), original.getHeight());
		operateOnLayer(original.blue, original.getWidth(), original.getHeight());
	}

	private void operateOnLayer(final double[] layer, final int width, final int height) {
		final double newLayer[] = Arrays.copyOf(layer, layer.length);
		for (int i = 1; i < layer.length; i++) {
			newLayer[i] = layer[i] * layer[i-1] < 0 ? 255 : 0;
		}
		for (int x = 1; x < width; x++) {
			for (int y = 1; y < height; y++) {
				final int i = y * width + x;
				newLayer[i] = newLayer[i] == 255 ? 255 : layer[i] * layer[i-width] < 0 ? 255 : 0;
			}
		}
		newLayer[0] = 0;
		for (int i = 0; i < layer.length; i++) {
			layer[i] = newLayer[i];
		}
	}
}
