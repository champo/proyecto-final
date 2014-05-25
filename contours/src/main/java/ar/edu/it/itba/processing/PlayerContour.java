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
    private int maxSpeed;
    private long sumSpeed;
    private int speedPoints;
    private final List<Point> lastPoints = new LinkedList<Point>();
    private HeatMap heatmap;
    private final int SPEED_POINTS = 5;
    private final int SCALE_MAP = 25;
    private double distance = 0;

    public double distanceMoved() {
        return distance;
    }

    public PlayerContour(int color, Rectangle rect) {
        super(color, rect);
        name = "NN";
        position = "0";
        team = "No team";
    }

    public PlayerContour(String name, String position, String team, int color, Rectangle rect) {
        super(color, rect);
        this.name = name;
        this.position = position;
        this.team = team;
    }

    public void setHeatMap(HeatMap heatmap) {
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

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public static PlayerContour aroundPoint(String name, String position, String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 6, 6, 12));
    }

    public static PlayerContour squareAroundPoint(String name, String position, String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 3, 6, 6));
    }

    public void addHistoricalPoint(final Point p) {
        getHeatMap().addPoint(p);
        lastPoints.add(p);
        if (lastPoints.size() > SPEED_POINTS) {
            lastPoints.remove(0);
        }
        speedPoints += 1;
        sumSpeed += calculateAverageSpeed();
    }

    public Object description() {
    	return toString();
    }

    @Override
    public String toString() {
    	if (isInitialized()) {
    		return name + "(" + team + ") @ (" + centroidX() + ", " + centroidY() + ")";
    	}
    	return name + "(" + team + ")";
    }

    private double calculateAverageSpeed() {
        if (lastPoints.size() < SPEED_POINTS) {
            return 0;
        }
        Point last = lastPoints.get(lastPoints.size() - 1);
        Point first = lastPoints.get(0);
        double distant = Math.sqrt(
            Math.pow(last.x - first.x, 2) +
            Math.pow(last.y - first.y, 2)
        );
        distance += distant / SPEED_POINTS * SCALE_MAP;
        maxSpeed = (int) Math.max(maxSpeed, distant * SCALE_MAP);
        return distant * SCALE_MAP;
    }
}
