package ar.edu.it.itba;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

		loadNextFrame();
	}

	private void loadNextFrame() {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
            	imagePanel.setImage(frameDecoder.nextFrame());
            }
        });
	}
}
