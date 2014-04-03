package ar.edu.it.itba.video;


public class LensCorrection extends AbstractFrameProviderDecorator {

	private double strength;

	public LensCorrection(final FrameProvider provider, final double strength) {
		super(provider);
		this.strength = strength;
	}

	public void setStrength(final double strength) {
		this.strength = strength;
	}

	@Override
	public int getRGB(int x, int y) {
        int imageWidth = provider.getWidth();
        int imageHeight = provider.getHeight();

	    int halfWidth = imageWidth / 2;
	    int halfHeight = imageHeight / 2;

		int newX = x - halfWidth;
		int newY = y - halfHeight;

	    double correctionRadius = Math.sqrt(Math.pow(imageWidth, 2) + Math.pow(imageHeight, 2)) / strength;

		double distance = Math.sqrt(Math.pow(newX, 2) + Math.pow(newY, 2));
		double r = distance / correctionRadius;
		double theta;
		if (r == 0) {
			theta = 1;
		} else {
			theta = Math.atan(r) / r;
		}

        int sourceX = (int) (halfWidth + theta * newX);
        int sourceY = (int) (halfHeight + theta * newY);
        return provider.getRGB(sourceX, sourceY);
	}

	@Override
	public int getHeight() {
		return provider.getHeight();
	}

	public int getWidth() {
		return provider.getWidth();
	}

	@Override
	public void nextFrame() {
		provider.nextFrame();
	}
}
