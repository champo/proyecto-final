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
    Double lensCorrection;
    List<HomeographyManager.Pair> points = new ArrayList<>();
    // Depth is in the direction of opposing goal areas
    // Width is between laterals
    Double fieldWidth;
    Double fieldDepth;

    public Double getFieldDepth() {
        return fieldDepth;
    }

    public Double getFieldWidth() {
        return fieldWidth;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public Double getLensCorrection() {
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

    public void setFieldDepth(Double fieldDepth) {
        this.fieldDepth = fieldDepth;
    }

    public void setFieldWidth(Double fieldWidth) {
        this.fieldWidth = fieldWidth;
    }
    
}
