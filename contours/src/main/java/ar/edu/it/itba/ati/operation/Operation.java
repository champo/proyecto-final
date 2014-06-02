package ar.edu.it.itba.ati.operation;

import java.awt.Container;

import ar.edu.it.itba.ati.Canvas;
import ar.edu.it.itba.ati.ChangeListener;

public abstract class Operation implements Cloneable {

	private ChangeListener listener;

	public void setListener(final ChangeListener listener) {
		this.listener = listener;
	}

	protected void didChange() {
		if (listener != null) {
			listener.changed();
		}
	}

	public abstract void operate(Canvas original);

	@Override
	public Operation clone() throws CloneNotSupportedException {
		return (Operation) super.clone();
	}

	public Container getPanel() {
		return null;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
