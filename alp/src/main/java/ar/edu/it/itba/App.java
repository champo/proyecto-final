package ar.edu.it.itba;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import sun.awt.VariableGridLayout;
import ar.edu.it.itba.tracker.AdaptiveLinearPredictors;
import ar.edu.it.itba.tracker.ObjectFrame;

/**
 * Hello world!
 *
 */
public class App extends JFrame
{
	private ImagePanel imagePanel;
	private FrameDecoder frameDecoder;
	private AdaptiveLinearPredictors predictors;

	public static void main( final String[] args )
    {
    	new App().run();
    }

	public void run() {
		setPreferredSize(new Dimension(1450, 800));

		frameDecoder = new FrameDecoder("/Users/juanpablocivile/Documents/ITBA/final/videos/phone_100fps.avi");
		imagePanel = new ImagePanel();
		imagePanel.setSize(800, 600);
		VariableGridLayout gridLayout= new VariableGridLayout(2, 1);
		gridLayout.setRowFraction(0, 0.9);
		gridLayout.setRowFraction(1, 0.1);
		getContentPane().setLayout(gridLayout);
		getContentPane().add(imagePanel);

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
		setVisible(true);

		ObjectFrame initial = new ObjectFrame(new Point(129, 103), new Point(75, 399), new Point(590, 178), new Point(538, 477));

		predictors = new AdaptiveLinearPredictors(initial);
		loadNextFrame();
	}

	private void loadNextFrame() {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	BufferedImage frame = frameDecoder.nextFrame();
            	ObjectFrame result = predictors.trackFrame(frame);
            	if (result != null) {
            		result.drawOnImage(frame);
            	}
				imagePanel.setImage(frame);
            }
        });
	}
}
