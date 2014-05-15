/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba.processing;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import ar.edu.it.itba.metrics.HeatMap;

/**
 *
 * @author eordano
 */
public class PlayerContour extends Contour {

    public final String name;
    public final String position;
    public final String team;
    private double maxSpeed;
    private double sumSpeed;
    private int speedPoints;
    private final List<Double> lastSpeeds = new LinkedList<Double>();
    private HeatMap heatmap;

    private final List<Point> lastPositions = new LinkedList<Point>();

    public PlayerContour(final int color, final Rectangle rect) {
        super(color, rect);
        name = "NN";
        position = "0";
        team = "No team";
    }

    public PlayerContour(final String name, final String position, final String team, final int color, final Rectangle rect) {
        super(color, rect);
        this.name = name;
        this.position = position;
        this.team = team;
    }

    public void setHeatMap(final HeatMap heatmap) {
        this.heatmap = heatmap;
    }

    public HeatMap getHeatMap() {
        return heatmap;
    }

    public int getAverageSpeed() {
        if (speedPoints == 0) {
            return 0;
        }
        return (int) (sumSpeed / speedPoints);
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void addPosition(final Point p) {
    	lastPositions.add(p);
    	if (lastPositions.size() > SPEED_POINTS) {
    		lastPositions.remove(0);
    	}

    	if (lastPositions.size() < 2) {
    		return;
    	}

    	Point last = lastPositions.get(lastPositions.size() - 2);
        double speed = p.distance(last);

        addSpeedData(speed);
    }

    public void addSpeedData(final double speed) {
        lastSpeeds.add(speed);
        maxSpeed = Math.max(maxSpeed, speed);

        if (lastSpeeds.size() == SPEED_POINTS) {
            lastSpeeds.remove(0);
        }
        sumSpeed += speed;
        speedPoints++;
    }

    private final int SPEED_POINTS = 5;


    public static PlayerContour aroundPoint(final String name, final String position, final String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 6, 6, 12));
    }

    public static PlayerContour squareAroundPoint(final String name, final String position, final String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 3, 6, 6));
    }

    public Object description() {
        if (isInitialized()) {
            return name + " (" + centroidX() + ", " + centroidY() + ")";
        }
        return name + "(" + team + ")";
    }
}
