package ar.edu.it.itba.video;

public abstract class AbstractFrameProviderDecorator implements FrameProvider {

	protected final FrameProvider provider;

	public AbstractFrameProviderDecorator(FrameProvider frameProvider) {
		this.provider = frameProvider;
	}
	
	@Override
	public int getHeight() {
		return provider.getHeight();
	}

	@Override
	public int getWidth() {
		return provider.getWidth();
	}

	@Override
	public void nextFrame() {
		provider.nextFrame();
	}

	@Override
	public int getType() {
		return provider.getType();
	}
}
