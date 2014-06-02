package ar.edu.it.itba.ati.provider;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface FrameProvider {

	public abstract BufferedImage nextFrame() throws IOException;

}
