package ar.edu.it.itba;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import sun.awt.VariableGridLayout;

/**
 * Hello world!
 *
 */
public class App extends JFrame
{
	private ImagePanel imagePanel;
	private FrameDecoder frameDecoder;

	private Point mark;
	private MouseListener mouseListener;

	public static void main( final String[] args )
    {
    	new App().run();
    }

	public void run() {
		setPreferredSize(new Dimension(1450, 800));

		frameDecoder = new FrameDecoder("src/main/resources/Slower10sec.mpeg");
		imagePanel = new ImagePanel();
		imagePanel.setSize(800, 600);
		VariableGridLayout gridLayout= new VariableGridLayout(2, 1);
		gridLayout.setRowFraction(0, 0.9);
		gridLayout.setRowFraction(1, 0.1);
		getContentPane().setLayout(gridLayout);
		getContentPane().add(imagePanel);

		mouseListener = new MouseListener() {

			@Override
			public void mouseReleased(final MouseEvent arg0) {
			}

			@Override
			public void mousePressed(final MouseEvent arg0) {
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
			}

			@Override
			public void mouseClicked(final MouseEvent arg0) {
				mark = arg0.getPoint();
				imagePanel.removeMouseListener(mouseListener);
				addNextFrameButton();
				System.out.println(mark);
			}
		};
		imagePanel.addMouseListener(mouseListener);

		pack();
		setVisible(true);

		// Skip first three frames cause it jumps to much
		frameDecoder.nextFrame();
		frameDecoder.nextFrame();
		frameDecoder.nextFrame();
		frameDecoder.nextFrame();

		loadNextFrame();
	}

	private void addNextFrameButton() {
		Button button = new Button("Next frame");
		button.setSize(new Dimension(100, 10));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				loadNextFrame();
			}
		});
		getContentPane().add(button);

		pack();
		repaint();
	}

	private void loadNextFrame() {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	BufferedImage frame = frameDecoder.nextFrame();
				imagePanel.setImage(frame);
            }
        });
	}
}
