/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import ar.edu.it.itba.HomeographyManager.Pair;
import ar.edu.it.itba.config.ConfigRetrieval;
import ar.edu.it.itba.config.SequenceSettings;
import ar.edu.it.itba.processing.Homography;
import ar.edu.it.itba.video.FrameDecoder;
import ar.edu.it.itba.video.FrameProvider;
import ar.edu.it.itba.video.LensCorrection;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;

/**
 *
 * @author eordano
 */
public class Main2 extends javax.swing.JFrame {

    FrameProvider frameDecoder;
    List<SequenceSettings> settings;
    SequenceSettings currentSettings;
    ImagePanel imagePanel;
    private HomeographyManager homeographyManager;
    boolean selectingPoint;
    /**
     * Creates new form Main2
     */
    public Main2() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel2 = new javax.swing.JPanel();
        homographySettingsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListPoints = new javax.swing.JList();
        newHomographyPointButton = new javax.swing.JButton();
        deleteHomographyPointButton = new javax.swing.JButton();
        jTextFieldCorrection = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jFixFieldSize = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabelSelectPoint = new javax.swing.JLabel();
        jTextFieldWidth = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldDepth = new javax.swing.JTextField();
        jButtonUpdateLensCorrection = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jPanelVideo = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jTabbedPane1.setEnabled(false);
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentResized(evt);
            }
        });

        jLabel1.setText("Select a file from saved settings:");

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jList1ValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel2.setText("Or open a new video file:");

        jFileChooser1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileChooser1ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jFileChooser1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jScrollPane1)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel1)
                                    .add(jLabel2))
                                .add(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jFileChooser1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Video", jPanel1);

        jPanel2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jPanel2ComponentResized(evt);
            }
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel2ComponentShown(evt);
            }
        });

        homographySettingsPanel.setPreferredSize(new java.awt.Dimension(150, 541));

        jLabel3.setText("List of Points:");

        jListPoints.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListPoints.setEnabled(false);
        jScrollPane2.setViewportView(jListPoints);

        newHomographyPointButton.setText("New Point");
        newHomographyPointButton.setEnabled(false);
        newHomographyPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newHomographyPointButtonActionPerformed(evt);
            }
        });

        deleteHomographyPointButton.setText("Delete");
        deleteHomographyPointButton.setEnabled(false);

        jTextFieldCorrection.setText("0.0");

        jLabel4.setText("Correction Factor");

        jFixFieldSize.setText("Fix Field Size");
        jFixFieldSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFixFieldSizeActionPerformed(evt);
            }
        });

        jButton2.setText("Continue");

        jLabelSelectPoint.setText("Select point in the field");

        jTextFieldWidth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldWidthActionPerformed(evt);
            }
        });
        jTextFieldWidth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldWidthPropertyChange(evt);
            }
        });

        jLabel6.setText("Field width");

        jLabel7.setText("Field depth");

        jTextFieldDepth.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldDepthActionPerformed(evt);
            }
        });
        jTextFieldDepth.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextFieldDepthPropertyChange(evt);
            }
        });

        jButtonUpdateLensCorrection.setText("Update");
        jButtonUpdateLensCorrection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateLensCorrectionActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout homographySettingsPanelLayout = new org.jdesktop.layout.GroupLayout(homographySettingsPanel);
        homographySettingsPanel.setLayout(homographySettingsPanelLayout);
        homographySettingsPanelLayout.setHorizontalGroup(
            homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homographySettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(homographySettingsPanelLayout.createSequentialGroup()
                        .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(homographySettingsPanelLayout.createSequentialGroup()
                                .add(newHomographyPointButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deleteHomographyPointButton)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(homographySettingsPanelLayout.createSequentialGroup()
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jTextFieldWidth)))
                        .addContainerGap())
                    .add(homographySettingsPanelLayout.createSequentialGroup()
                        .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(homographySettingsPanelLayout.createSequentialGroup()
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jTextFieldDepth))
                            .add(homographySettingsPanelLayout.createSequentialGroup()
                                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(homographySettingsPanelLayout.createSequentialGroup()
                                        .add(6, 6, 6)
                                        .add(jLabelSelectPoint))
                                    .add(homographySettingsPanelLayout.createSequentialGroup()
                                        .add(jLabel4)
                                        .add(18, 18, 18)
                                        .add(jTextFieldCorrection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 202, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(0, 0, Short.MAX_VALUE)))
                        .add(1, 1, 1))
                    .add(homographySettingsPanelLayout.createSequentialGroup()
                        .add(jLabel3)
                        .add(0, 0, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, homographySettingsPanelLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(jFixFieldSize))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, homographySettingsPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButtonUpdateLensCorrection)
                .addContainerGap())
        );
        homographySettingsPanelLayout.setVerticalGroup(
            homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homographySettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldWidth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(5, 5, 5)
                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldDepth, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7))
                .add(5, 5, 5)
                .add(jFixFieldSize)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextFieldCorrection, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButtonUpdateLensCorrection)
                .add(14, 14, 14)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 245, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homographySettingsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(newHomographyPointButton)
                    .add(deleteHomographyPointButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabelSelectPoint)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 37, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        jPanelVideo.setPreferredSize(new java.awt.Dimension(1000, 800));

        org.jdesktop.layout.GroupLayout jPanelVideoLayout = new org.jdesktop.layout.GroupLayout(jPanelVideo);
        jPanelVideo.setLayout(jPanelVideoLayout);
        jPanelVideoLayout.setHorizontalGroup(
            jPanelVideoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1000, Short.MAX_VALUE)
        );
        jPanelVideoLayout.setVerticalGroup(
            jPanelVideoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 800, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(jPanelVideo);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 549, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homographySettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 209, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(homographySettingsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 29, Short.MAX_VALUE))
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Homography", jPanel2);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 770, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 576, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Tracking", jPanel3);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 770, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 576, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Visualize Psi", jPanel4);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 791, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 603, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jFileChooser1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileChooser1ActionPerformed

        if (evt.getActionCommand().equals("CancelSelection")) {
            System.exit(0);
        }
        String path = jFileChooser1.getSelectedFile().getAbsolutePath();
        frameDecoder = new LensCorrection(new FrameDecoder(path), 0);
        currentSettings = new SequenceSettings();
        currentSettings.setName("Unnamed_1");
        currentSettings.setPath(path);
        homeographyManager = new HomeographyManager();
        for (Pair p : currentSettings.getPoints()) {
            homeographyManager.setMapping(p.image, p.mapped);
        }
        loadVideo();
    }//GEN-LAST:event_jFileChooser1ActionPerformed

    private void jList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jList1ValueChanged

        currentSettings = settings.get(jList1.getSelectedIndex());
        homeographyManager = new HomeographyManager();
        Double data = currentSettings.getLensCorrection();
        double value = data == null ? data : 0;
        frameDecoder = new LensCorrection(new FrameDecoder(currentSettings.getPath()), value);
        loadVideo();
    }//GEN-LAST:event_jList1ValueChanged

    private void jFixFieldSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFixFieldSizeActionPerformed
                                                                
        Double data;
        try {
            data = Double.valueOf(jTextFieldWidth.getText());
        } catch (NumberFormatException e) {
            data = null;
        }
        currentSettings.setFieldWidth(data);
        try {
            data = Double.valueOf(jTextFieldDepth.getText());
        } catch (NumberFormatException e) {
            data = null;
        }
        currentSettings.setFieldDepth(data);
        tryEnableButtons();
    }//GEN-LAST:event_jFixFieldSizeActionPerformed

    private void newHomographyPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newHomographyPointButtonActionPerformed
        jLabelSelectPoint.setVisible(true);
        selectingPoint = true;
    }//GEN-LAST:event_newHomographyPointButtonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized

        jTabbedPane1.setSize(getSize());
    }//GEN-LAST:event_formComponentResized

    private void jTabbedPane1ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentResized

        jTabbedPane1.getSelectedComponent().setSize(jTabbedPane1.getSize());
    }//GEN-LAST:event_jTabbedPane1ComponentResized

    private void jTextFieldWidthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldWidthActionPerformed
    }//GEN-LAST:event_jTextFieldWidthActionPerformed

    private void jPanel2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel2ComponentShown

    private void jPanel2ComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2ComponentResized
        jScrollPane3.setSize(getWidth() - 230, getHeight() - 60);
        homographySettingsPanel.setSize(230, getHeight());
    }//GEN-LAST:event_jPanel2ComponentResized

    private void jTextFieldDepthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldDepthActionPerformed
                                                             

    }//GEN-LAST:event_jTextFieldDepthActionPerformed

    private void jTextFieldDepthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldDepthPropertyChange

    }//GEN-LAST:event_jTextFieldDepthPropertyChange

    private void jTextFieldWidthPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextFieldWidthPropertyChange

    }//GEN-LAST:event_jTextFieldWidthPropertyChange

    private void jButtonUpdateLensCorrectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateLensCorrectionActionPerformed
        Double data;
        try {
            data = Double.valueOf(jTextFieldCorrection.getText());
        } catch (NumberFormatException e) {
            data = null;
        }
        if (data != null) {
            currentSettings.setLensCorrection(data);
            setupImageHomo(((LensCorrection) frameDecoder).setStrength(data));
        }
    }//GEN-LAST:event_jButtonUpdateLensCorrectionActionPerformed

    private void loadVideo() {
        jTabbedPane1.setSelectedIndex(1);
        if (currentSettings.getLensCorrection() != null) {
            Double data = currentSettings.getLensCorrection();
            double value = data == null ? 0 : data;
            jTextFieldCorrection.setText(Double.toString(data));
        }
        
        if (currentSettings.getFieldDepth() != null) {
            Double data = currentSettings.getFieldDepth();
            double value = data == null ? 0 : data;
            jTextFieldDepth.setText(Double.toString(data));
        }
        
        if (currentSettings.getFieldWidth() != null) {
            Double data = currentSettings.getFieldWidth();
            double value = data == null ? 0 : data;
            jTextFieldWidth.setText(Double.toString(data));
        }
        
        imagePanel = new ImagePanel();
        BufferedImage frame = frameDecoder.nextFrame();
        Dimension frameDim = new Dimension(frame.getWidth(), frame.getHeight());
        imagePanel.setSize(frameDim);
        jPanelVideo.setPreferredSize(frameDim);
        jScrollPane3.setPreferredSize(frameDim);
        jPanelVideo.add(imagePanel);
        imagePanel.setImage(frame);
        imagePanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectingPoint) {
                    PointInFieldDialogue.showUp(Main2.this, e.getPoint(),
                            currentSettings.getFieldWidth().intValue(), currentSettings.getFieldDepth().intValue(),
                            new PointInFieldDialogue.PointInFieldListener() {

                        @Override
                        public void getResult(Pair p) {
                            homeographyManager.setMapping(p.image, p.mapped);
                            jListPoints.updateUI();
                            // Force redraw
                            setupImageHomo(((LensCorrection) frameDecoder).setStrength(currentSettings.getLensCorrection()));
                            selectingPoint = false;
                        }
                    });
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        jListPoints.setModel(homeographyManager.getListModel());                                                  
        jLabelSelectPoint.setVisible(false);
        
        tryEnableButtons();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */

       final Main2 instance = new Main2();
        try {
            instance.settings = ConfigRetrieval.retrieveFromSource(new FileInputStream(new File("./settings.txt")));
        } catch (IOException e) {
            instance.settings = new LinkedList<>();
            Logger.getLogger("Main2").warning("Unable to retrieve settings!");
        }
       instance.jList1.setModel(new AbstractListModel() {

            @Override
            public int getSize() {
                return instance.settings.size();
            }

            @Override
            public Object getElementAt(int index) {
                return instance.settings.get(index).getName() + "(" +
                        instance.settings.get(index).getPath() + ")";
            }
        });
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                instance.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteHomographyPointButton;
    private javax.swing.JPanel homographySettingsPanel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButtonUpdateLensCorrection;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JButton jFixFieldSize;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelSelectPoint;
    private javax.swing.JList jList1;
    private javax.swing.JList jListPoints;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanelVideo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextFieldCorrection;
    private javax.swing.JTextField jTextFieldDepth;
    private javax.swing.JTextField jTextFieldWidth;
    private javax.swing.JButton newHomographyPointButton;
    // End of variables declaration//GEN-END:variables

    private void tryEnableButtons() {
        
        if (currentSettings.getFieldDepth() != null && currentSettings.getFieldWidth() != null) {
            jTextFieldDepth.setEnabled(false);
            jTextFieldWidth.setEnabled(false);
            jFixFieldSize.setEnabled(false);
            newHomographyPointButton.setEnabled(true);
            deleteHomographyPointButton.setEnabled(true);
        }
    }

    private void setupImageHomo(BufferedImage newImage) {
        if (homeographyManager.getListModel().getSize() > 3) {
            Homography homography = homeographyManager.calculateHomography();
            // Draw field limits
            for (int i = 0; i < 500; i++) {
                Point inverseApply = homography.inverseApply(0, (int)(i / currentSettings.getFieldDepth()));
                newImage.setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                inverseApply = homography.inverseApply(currentSettings.getFieldWidth().intValue(), (int) (i / currentSettings.getFieldDepth()));
                newImage.setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                inverseApply = homography.inverseApply((int) (i / currentSettings.getFieldWidth()), 0);
                newImage.setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                inverseApply = homography.inverseApply((int) (i / currentSettings.getFieldWidth()), currentSettings.getFieldDepth().intValue());
                newImage.setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                
                // Middle of the field
                inverseApply = homography.inverseApply((int) (i / currentSettings.getFieldWidth()), currentSettings.getFieldDepth().intValue() / 2);
                newImage.setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
            }
        }
        imagePanel.setImage(newImage);
    }
}
