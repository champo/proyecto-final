package ar.edu.it.itba;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import ar.edu.it.itba.processing.Contour;
import ar.edu.it.itba.processing.PlayerContour;
import java.util.HashMap;
import java.util.Map;

public class ImageOperations {

    /*public static Color phiColoring[] = new Color[3 * 3 * 3];
    static {
        int index = 0;
        int fraction = 255 / 3;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    phiColoring[index++] = new Color(fraction * i, fraction * j, fraction * k);
                }
            }
        }
    };
    */
	public static Color phiColoring[] = new Color[] {
        new Color(255, 0, 0),
        new Color(255, 0, 255),
        new Color(0, 255, 0),
        new Color(0, 255, 255),
        new Color(255, 255, 255),
        new Color(128, 128, 128),
        new Color(0,0,0)
    };
    public static final Map<String, Color> teamColor = new HashMap<String, Color>();

    public static void drawContourOnBuffer(final BufferedImage image, final Contour c) {

        if (teamColor.get(((PlayerContour) c).team) == null) {
            teamColor.put(((PlayerContour) c).team, phiColoring[teamColor.size()]);
        }
        Color color = teamColor.get(((PlayerContour) c).team);
        paintContour(image, c, color);
    }
    public static void paintContour(final BufferedImage image, final Contour c, final Color color) {   
        for (Point p : c.getLin()) {
            int x = p.x;
            int y = p.y;
            image.setRGB(x, y, color.getRGB());
        }
        for (Point p : c.getLout()) {
            int x = p.x;
            int y = p.y;
            image.setRGB(x, y, color.getRGB());
        }
    }

    static void drawRectangle(BufferedImage frame, String team, Point p) {
        if (teamColor.get(team) == null) {
            teamColor.put(team, phiColoring[teamColor.size()]);
        }
        Color color = teamColor.get(team);
        for (int i = -7; i <= 7; i+=14) {
            int x = p.x + i;
            if (x >= 0 && x < frame.getWidth()) {
                for (int j = -14; j <= 14; j++) {
                    int y = p.y + j;
                    if (y >= 0 && y < frame.getHeight()) {
                        frame.setRGB(x, y, color.getRGB());
                    }
                }
            }
        }
        for (int i = -7; i <= 7; i++) {
            int x = p.x + i;
            if (x >= 0 && x < frame.getWidth()) {
                for (int j = -14; j <= 14; j+=28) {
                    int y = p.y + j;
                    if (y >= 0 && y < frame.getHeight()) {
                        frame.setRGB(x, y, color.getRGB());
                    }
                }
            }
        }
    }
}
