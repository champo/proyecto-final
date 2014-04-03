package ar.edu.it.itba.video;


public interface FrameProvider {

	public void nextFrame();

	public int getWidth();
	public int getHeight();
	public int getRGB(int x, int y);

	public int getType();
}