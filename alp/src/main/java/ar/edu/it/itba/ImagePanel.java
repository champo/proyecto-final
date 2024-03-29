package ar.edu.it.itba;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private BufferedImage image;

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        if (image != null) {
        	g.drawImage(image, 0, 0, null);
        }
    }

    public void setImage(final BufferedImage image) {
		this.image = image;
		repaint();
	}
}
