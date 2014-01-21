/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba.config;

import ar.edu.it.itba.HomeographyManager;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eordano
 */
public class SequenceSettings {
    String name;
    String path;
    double lensCorrection;
    List<HomeographyManager.Pair> points = new ArrayList<>();
    // Depth is in the direction of opposing goal areas
    // Width is between laterals
    double fieldWidth;
    double fieldDepth;

    public double getFieldDepth() {
        return fieldDepth;
    }

    public double getFieldWidth() {
        return fieldWidth;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public double getLensCorrection() {
        return lensCorrection;
    }

    public void setLensCorrection(double lensCorrection) {
        this.lensCorrection = lensCorrection;
    }

    public List<HomeographyManager.Pair> getPoints() {
        return points;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
