package ar.edu.it.itba.ati;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import ar.edu.it.itba.ati.operation.Operation;
import ar.edu.it.itba.ati.operation.tp1.LinearNormalization;
import ar.edu.it.itba.ati.provider.CanvasProvider;

public class Operations implements Cloneable {

	private final List<Operation> ops = new ArrayList<Operation>();

	private final CanvasProvider genesis;

	private ChangeListener listener;

	private final int height;

	private final int width;

	private boolean dirty = true;

	private Canvas cache;

	public Operations(final CanvasProvider genesis) {
		this.genesis = genesis;
		Canvas original = genesis.getCanvas();
		height = original.height;
		width = original.width;
	}

	public void setListener(final ChangeListener listener) {
		this.listener = listener;
	}

	public void add(final Operation op) {
		op.setListener(new ChangeListener() {

			@Override
			public void changed() {
				uglyMutator();
			}
		});
		ops.add(op);
		uglyMutator();
	}

	public void remove(final int i) {
		ops.remove(i);
		uglyMutator();
	}

	private void uglyMutator() {
		dirty = true;
		cache = null;
		if (listener != null) {
			listener.changed();
		}
	}

	public Canvas calculateCanvas() {

		if (!dirty && cache != null) {
			return cache;
		}

		Canvas result = genesis.getCanvas();
		for (Operation op : ops) {
			op.operate(result);
		}

		for (int i = 0; i < result.size; i++) {
			if (result.red[i] < 0 || result.red[i] > 255
					|| result.green[i] < 0 || result.green[i] > 255
					|| result.blue[i] < 0 || result.blue[i] > 255) {
				new LinearNormalization(false).operate(result);
				break;
			}
		}

		cache = result;

		return result;

	}

	public BufferedImage calculateResult() {
		return calculateCanvas().getBufferedImage();
	}

	public CanvasProvider getGenesis() {
		return genesis;
	}

	public List<Operation> getOps() {
		return ops;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
