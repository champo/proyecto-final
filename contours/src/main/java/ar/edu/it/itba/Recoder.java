package ar.edu.it.itba;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ar.edu.it.itba.video.BlackOutOutskirts;
import ar.edu.it.itba.video.FrameDecoder;
import ar.edu.it.itba.video.FrameProvider;
import ar.edu.it.itba.video.LensCorrection;

public class Recoder {

	public static void main(String[] args) {

        List<Point> points = new LinkedList<Point>();
        points.add(new Point(425, 40));
        points.add(new Point(54, 321));
        points.add(new Point(1518, 345));
        points.add(new Point(1147, 52));
        List<Point> firstPoints = new LinkedList<Point>();
        firstPoints.add(new Point(200, 375));
        firstPoints.add(new Point(200, 750));
        firstPoints.add(new Point(1750, 750));
        firstPoints.add(new Point(1750, 375));
		FrameProvider frameDecoder =
                new BlackOutOutskirts(
	                new LensCorrection(
                        new BlackOutOutskirts(
	                        new FrameDecoder("/Users/eordano/Downloads/Boca1.mp4")
	                    , firstPoints)
                    , 1.91)
	            , points
	        ) 
        ;
		try {
			SequenceEncoder encoder = new SequenceEncoder(new File("video.mkv"));
			frameDecoder.nextFrame();
			for (int i = 0; i < 24*60; i++) { // 1 minute
				BufferedImage bi = new BufferedImage(frameDecoder.getWidth(), frameDecoder.getHeight(), frameDecoder.getType());
				for (int x = 0; x < bi.getWidth(); x++) {
					for (int y = 0; y < bi.getHeight(); y++) {
						bi.setRGB(x, y, frameDecoder.getRGB(x, y));
					}
				}
				encoder.encodeImage(bi);
				System.out.println("Encoded frame " + i + " out of " + 24*60);
			}
			encoder.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
