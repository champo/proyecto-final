/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import ar.edu.it.itba.processing.PlayerContour;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.image.BufferedImage;

/**
 *
 * @author eordano
 */
public class PlayerStatsDialog extends javax.swing.JDialog {
    private final PlayerContour contour;

    /**
     * Creates new form PlayerStats
     */
    public PlayerStatsDialog(java.awt.Frame parent, boolean modal, PlayerContour contour) {
        super(parent, modal);
        initComponents();
        this.contour = contour;
        maximumSpeedLabel.setText("" + contour.getMaxSpeed() + " cm/s");
        nameLabel.setText(contour.name);
        positionLabel.setText(contour.position);
        teamLabel.setText(contour.team);
        averageSpeedLabel.setText("" + contour.getAverageSpeed() + " cm/s");
        distanceLabel.setText(contour.distanceMoved() + " cm");
        ImagePanel heat = new ImagePanel();
        BufferedImage image = contour.getHeatMap().getFrame();
        heat.setImage(image);
        heat.setSize(image.getWidth(), image.getHeight());
        jPanel1.add(heat);
        repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        teamLabel = new javax.swing.JLabel();
        positionLabel = new javax.swing.JLabel();
        averageSpeedLabel = new javax.swing.JLabel();
        maximumSpeedLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        distanceLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setText("Nombre:");

        jLabel2.setText("Equipo:");

        jLabel3.setText("Posición:");

        jLabel4.setText("Velocidad máxima:");

        jLabel5.setText("Velocidad promedio:");

        nameLabel.setText("jLabel6");

        teamLabel.setText("teamLabel");

        positionLabel.setText("jLabel6");

        averageSpeedLabel.setText("jLabel6");

        maximumSpeedLabel.setText("jLabel6");

        jLabel6.setText("Distancia recorrida:");

        distanceLabel.setText("jLabel6");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 331, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel2)
                            .add(jLabel3)
                            .add(jLabel4)
                            .add(jLabel5)
                            .add(jLabel6))
                        .add(27, 27, 27)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(distanceLabel)
                            .add(averageSpeedLabel)
                            .add(maximumSpeedLabel)
                            .add(positionLabel)
                            .add(teamLabel)
                            .add(nameLabel))
                        .add(0, 391, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(teamLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(positionLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel5))
                    .add(layout.createSequentialGroup()
                        .add(maximumSpeedLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(averageSpeedLabel)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(distanceLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel averageSpeedLabel;
    private javax.swing.JLabel distanceLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel maximumSpeedLabel;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel positionLabel;
    private javax.swing.JLabel teamLabel;
    // End of variables declaration//GEN-END:variables
}
