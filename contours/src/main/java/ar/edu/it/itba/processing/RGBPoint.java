package ar.edu.it.itba.processing;

public class RGBPoint {


	private static final boolean rgb = false;

	public final int red;

	public final int blue;

	public final int green;

	public RGBPoint(final int rgb) {

		int red = rgb >> 16 & 0xFF;
		int green = rgb >> 8 & 0xFF;
		int blue = rgb & 0xFF;

		if (RGBPoint.rgb) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		} else {
			int[] hsv = new int[3];
			rgb2hsv(red, green, blue, hsv);

			this.red = hsv[0];
			this.green = hsv[1];
			this.blue = 0; //hsv[2];

			assert(0 <= this.red && this.red < 255);
			assert(0 <= this.green && this.green < 255);
			assert(0 <= this.blue && this.blue < 255);
		}
	}


	public RGBPoint(final int red, final int green, final int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	private void rgb2hsv(final int red, final int green , final int blue, final int hsv[]) {

		float min;    //Min. value of RGB
		float max;    //Max. value of RGB
		float delMax; //Delta RGB value

		float r = red / 255.0f;
		float g = green / 255.0f;
		float b = blue / 255.0f;

		if (r > g) {
			min = g;
			max = r;
		} else {
			min = r;
			max = g;
		}
		if (b > max) {
			max = b;
		}
		if (b < min) {
			min = b;
		}

		delMax = max - min;

		float H = 0, S;
		float V = max;

		if ( delMax == 0 ) {
			H = 0;
			S = 0;
		} else {
			S = delMax / V;
			if ( r == max ) {
				H = ((g - b) / delMax) % 6;
			} else if ( g == max ) {
				H = 2 + (b - r) / delMax;
			} else if ( b == max ) {
				H = 4 +  (r - g) / delMax;
			}

			H *= 60;
		}

		hsv[0] = (int) (H * 255.0f / 360.0f);
		hsv[1] = (int) (S * 255);
		hsv[2] = (int) (V * 255);
	}

	public double diff(final RGBPoint reference) {
		return Math.abs(red - reference.red)
				+ Math.abs(green - reference.green)
				+ Math.abs(blue - reference.blue);
	}
}
