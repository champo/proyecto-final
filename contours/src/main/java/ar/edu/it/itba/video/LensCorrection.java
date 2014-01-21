package ar.edu.it.itba.video;

import java.awt.image.BufferedImage;

public class LensCorrection implements FrameProvider {

	private final FrameProvider provider;

        private BufferedImage backup;
	private double strength;

	public LensCorrection(final FrameProvider provider, final double strength) {
		super();
		this.provider = provider;
		this.strength = strength;
	}

	public BufferedImage setStrength(final double strength) {
		this.strength = strength;
                return this.apply(backup, strength);
	}

	@Override
	public BufferedImage nextFrame() {
		return apply(provider.nextFrame(), strength);
	}

	private BufferedImage apply(final BufferedImage original, final double strength) {

            this.backup = original;
            int imageWidth = original.getWidth();
            int imageHeight = original.getHeight();
            BufferedImage copy = new BufferedImage(imageWidth, imageHeight, original.getType());

	    int halfWidth = imageWidth / 2;
	    int halfHeight = imageHeight / 2;

	    double correctionRadius = Math.sqrt(Math.pow(imageWidth, 2) + Math.pow(imageHeight, 2)) / strength;

	    for (int x = 0; x < imageWidth; x++) {
	    	for (int y = 0; y < imageHeight; y++) {
	    		int newX = x - halfWidth;
	    		int newY = y - halfHeight;

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
	    		copy.setRGB(x, y, original.getRGB(sourceX, sourceY));
	    	}
	    }

		return copy;
	}

}
