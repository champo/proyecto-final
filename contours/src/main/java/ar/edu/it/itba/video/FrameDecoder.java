package ar.edu.it.itba.video;

import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

import javax.management.RuntimeErrorException;

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

public class FrameDecoder implements FrameProvider {

	private static final int BUFFER_MAX_SIZE = 4;
	private static final int BUFFER_MIN_SIZE = 1;
	private BufferedImage buffer;

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
	private final Queue<BufferedImage> frameQueue = new ConcurrentLinkedDeque<BufferedImage>();
	private final Semaphore semaphore = new Semaphore(BUFFER_MAX_SIZE);

	public FrameDecoder(final String path) {
		reader = ToolFactory.makeReader(path);
		reader.addListener(new IMediaListenerImplementation());
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		while (frameQueue.size() < BUFFER_MAX_SIZE && reader.readPacket() == null);
		initRetrieveThread();
	}

	public FrameDecoder(final String path, int skipFrames) {
		reader = ToolFactory.makeReader(path);
		reader.addListener(new IMediaListenerImplementation());
		reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
		while (frameQueue.size() < BUFFER_MAX_SIZE && reader.readPacket() == null);	
		for (int i = 0; i < skipFrames; i++) {
			frameQueue.poll();
		}
		initRetrieveThread();
	}

	private void initRetrieveThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						semaphore.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
					while (frameQueue.size() < BUFFER_MAX_SIZE && reader.readPacket() == null);
				}
			}
		}).start();
	}

	@Override
	public void nextFrame() {
		if (frameQueue.size() < BUFFER_MIN_SIZE) {
			semaphore.release();
		}
		while (frameQueue.isEmpty());
		buffer = frameQueue.poll();
	}

	@Override
	public int getHeight() {
		return buffer.getHeight();
	}
	@Override
	public int getWidth() {
		return buffer.getWidth();
	}
	@Override
	public int getRGB(int x, int y) {
		return buffer.getRGB(x, y);
	}
	@Override
	public int getType() {
		return buffer.getType();
	}
}
