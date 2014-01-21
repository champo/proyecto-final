/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba.config;

import ar.edu.it.itba.HomeographyManager;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author eordano
 */
public class ConfigRetrieval {


    public static void saveToSink(List<SequenceSettings> settings, OutputStream sink) throws IOException {

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(sink));
        for (SequenceSettings sequence : settings) {
            writer.write("name " + sequence.getName());
            writer.write("path " + sequence.getPath());
            writer.write("field_width " + sequence.getFieldWidth());
            writer.write("field_depth " + sequence.getFieldDepth());
            writer.write("lens " + sequence.getLensCorrection());
            for (HomeographyManager.Pair pair : sequence.getPoints()) {
                StringBuilder buffer = new StringBuilder("homography_point ");
                buffer.append(pair.image.x); buffer.append(" ");
                buffer.append(pair.image.y); buffer.append(" ");
                buffer.append(pair.mapped.x); buffer.append(" ");
                buffer.append(pair.mapped.y);
                writer.write(buffer.toString());
            }
        }
    }

    public static List<SequenceSettings> retrieveFromSource(InputStream source) throws IOException {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(source));
        String line;
        List<SequenceSettings> returnList = new ArrayList<>();
        SequenceSettings settings = new SequenceSettings();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                returnList.add(settings);
                settings = new SequenceSettings();
            } else {
                String[] data = line.split(" ");
                if (data[0].equals("name")) {
                    settings.name = data[1];
                }
                if (data[0].equals("path")) {
                    settings.path = data[1];
                }
                if (data[0].equals("homography_point")) {
                    settings.points.add(new HomeographyManager.Pair(
                            new Point(Integer.valueOf(data[1]), Integer.valueOf(data[2])),
                            new Point(Integer.valueOf(data[3]), Integer.valueOf(data[4]))
                    ));
                }
                if (data[0].equals("field_width")) {
                    settings.fieldWidth = Double.valueOf(data[1]);
                }
                if (data[0].equals("field_depth")) {
                    settings.fieldDepth = Double.valueOf(data[1]);
                }
                if (data[0].equals("lens")) {
//                    settings.lensCorrection = Double.valueOf(data[1]);
                }
            }
        }
        return returnList;
    }
}
