package ar.edu.it.itba.video;

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

public class FrameDecoder implements FrameProvider {

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

	@Override
	public BufferedImage nextFrame() {
		while (frameQueue.isEmpty() && reader.readPacket() == null);

		return frameQueue.poll();
	}

}
