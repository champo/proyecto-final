package ar.edu.it.itba.video;

import java.awt.image.BufferedImage;

public interface FrameProvider {

	public abstract BufferedImage nextFrame();

}