/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba.processing;

import ar.edu.it.itba.metrics.HeatMap;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

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
    private List<Long> lastSpeeds = new LinkedList<Long>();
    private HeatMap heatmap;

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

    public void addSpeedData(int speed) {
        lastSpeeds.add((long) speed);
        if (lastSpeeds.size() == SPEED_POINTS) {
            maxSpeed = Math.max(maxSpeed, calculateAverageSpeed());
            lastSpeeds.remove(0);
        }
        sumSpeed += speed;
        speedPoints++;
    }

    private final int SPEED_POINTS = 5;
    

    public static PlayerContour aroundPoint(String name, String position, String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 6, 6, 12));
    }

    public static PlayerContour squareAroundPoint(String name, String position, String team, final int color, final Point point) {
            return new PlayerContour(name, position, team, color, new Rectangle(point.x - 3, point.y - 3, 6, 6));
    }

    public Object description() {
        if (isInitialized()) {
            return name + " (" + centroidX() + ", " + centroidY() + ")";
        }
        return name + "(" + team + ")";
    }

    private int calculateAverageSpeed() {
        long sum = 0;
        for (long i : lastSpeeds) {
            sum += i;
        }
        return (int) (sum / lastSpeeds.size());
    }
}
