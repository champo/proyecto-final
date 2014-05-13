/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba.processing;

import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author eordano
 */
public class NamedContour extends Contour {
    public final String name;
    public final String position;
    public final String team;

    public NamedContour(int color, Rectangle rect) {
        super(color, rect);
        name = "NN";
        position = "0";
        team = "No team";
    }

    public NamedContour(String name, String position, String team, int color, Rectangle rect) {
        super(color, rect);
        this.name = name;
        this.position = position;
        this.team = team;
    }

    public static NamedContour aroundPoint(String name, String position, String team, final int color, final Point point) {
            return new NamedContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 6, 6, 12));
    }

    public static NamedContour squareAroundPoint(String name, String position, String team, final int color, final Point point) {
            return new NamedContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 3, 6, 6));
    }

    public Object description() {
        if (isInitialized()) {
            return name + " (" + centroidX() + ", " + centroidY() + ")";
        }
        return name + "(" + team + ")";
    }
}
