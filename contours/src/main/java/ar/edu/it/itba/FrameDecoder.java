package ar.edu.it.itba;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

import com.xuggle.mediatool.IMediaListener;
import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IFlushEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IReadPacketEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.IWriteHeaderEvent;
import com.xuggle.mediatool.event.IWritePacketEvent;
import com.xuggle.mediatool.event.IWriteTrailerEvent;

public class FrameDecoder {

	private final class IMediaListenerImplementation implements IMediaListener {
		@Override
		public void onWriteTrailer(final IWriteTrailerEvent arg0) {
		}

		@Override
		public void onWritePacket(final IWritePacketEvent arg0) {
		}

		@Override
		public void onWriteHeader(final IWriteHeaderEvent arg0) {
		}

		@Override
		public void onVideoPicture(final IVideoPictureEvent arg0) {
			frameQueue.add(arg0.getImage());
		}

		@Override
		public void onReadPacket(final IReadPacketEvent arg0) {
		}

		@Override
		public void onOpenCoder(final IOpenCoderEvent arg0) {
		}

		@Override
		public void onOpen(final IOpenEvent arg0) {
		}

		@Override
		public void onFlush(final IFlushEvent arg0) {
		}

		@Override
		public void onCloseCoder(final ICloseCoderEvent arg0) {
		}

		@Override
		public void onClose(final ICloseEvent arg0) {
		}

		@Override
		public void onAudioSamples(final IAudioSamplesEvent arg0) {
		}

		@Override
		public void onAddStream(final IAddStreamEvent arg0) {
		}
	}

	private final IMediaReader reader;
	private final Queue<BufferedImage> frameQueue = new ArrayDeque<BufferedImage>();

	public FrameDecoder(final String path) {
		reader = ToolFactory.makeReader(path);
		reader.addListener(new IMediaListenerImplementation());
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
	}

	public BufferedImage nextFrame() {
		while (frameQueue.isEmpty() && reader.readPacket() == null);

		BufferedImage original = frameQueue.poll();
		int imageWidth = original.getWidth();
		int imageHeight = original.getHeight();
		BufferedImage copy = new BufferedImage(imageWidth, imageHeight, original.getType());

	    int halfWidth = imageWidth / 2;
	    int halfHeight = imageHeight / 2;
	    double strength = 2.5;

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
