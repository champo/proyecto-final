package ar.edu.it.itba.ati.operation.tp1;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.operation.Operation;

public class DynamicRangeCompression extends Operation {

	@Override
	public void operate(final Canvas original) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		final int size = original.size;
		for (int i = 0; i < size; i++) {
			min = Math.min(min, original.red[i]);
			min = Math.min(min, original.green[i]);
			min = Math.min(min, original.blue[i]);
			max = Math.max(max, original.red[i]);
			max = Math.max(max, original.green[i]);
			max = Math.max(max, original.blue[i]);
		}
		drcOnArray(original.red, min, max, size);
		drcOnArray(original.blue, min, max, size);
		drcOnArray(original.green, min, max, size);
	}

	private void drcOnArray(final double[] array, final double min, final double max, final int size) {
		final double c = 255 / Math.log(1 + max);
		for (int x = 0; x < size; x++) {
			array[x] = c * Math.log(1 + array[x]);
		}
	}

	@Override
	public String toString() {
		return "Dynamic Range Compression";
	}
}
