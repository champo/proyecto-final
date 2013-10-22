/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Button;
import java.awt.Color;
import static java.awt.Component.CENTER_ALIGNMENT;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author eordano
 */
public class MainApp extends javax.swing.JFrame {

    private Point currentImagePoint;
    private Point currentMappedPoint;

    /**
     * Creates new form MainApp
     */
    public MainApp() {
        initComponents();
    }

    public static Color phiColoring[] = new Color[] {
        new Color(0,0,0),
        new Color(255, 0, 0),
        new Color(255, 0, 255),
        new Color(0, 255, 0),
        new Color(0, 255, 255),
        new Color(255, 255, 255),
        new Color(128, 128, 128)
    };
    private ImagePanel imagePanel;
    private ImagePanel soccerFieldPanel;
    private ImagePanel phiPanel;
    private FrameDecoder frameDecoder;

    private File outFile;
    private OutputStream outBuffer;
    private BufferedImage firstFrame;
    private ActiveContour ac;

    private final List<Contour> contour = new ArrayList<Contour>();

    private MouseListener mouseListener;
    private HomeographyManager homeographyManager;
    private int selected = 1;
    int framesElapsed = 0;

    private boolean selectingFirst = true;
    private boolean selectingPoint = false;
    private Button startTrackingButton;
	protected Homography homeography;
	protected boolean mappingPoint;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        homeographyMappingPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        pointsList = new javax.swing.JList();
        newPointButton = new javax.swing.JButton();
        deletePointButton = new javax.swing.JButton();
        calculateButton = new javax.swing.JButton();
        mapButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        soccerFieldContainer = new javax.swing.JPanel();
        videoControlPanel = new javax.swing.JPanel();
        phiImagePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        imageContainerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Puntos identificados en la cancha");

        pointsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(pointsList);

        newPointButton.setText("New");
        newPointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPointButtonActionPerformed(evt);
            }
        });

        deletePointButton.setText("Delete");
        deletePointButton.setEnabled(false);
        deletePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePointButtonActionPerformed(evt);
            }
        });

        calculateButton.setText("Calculate");
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        mapButton.setText("Map point");
        mapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mapButtonActionPerformed(evt);
            }
        });

        jScrollPane3.setMaximumSize(new java.awt.Dimension(403, 551));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(403, 551));

        org.jdesktop.layout.GroupLayout soccerFieldContainerLayout = new org.jdesktop.layout.GroupLayout(soccerFieldContainer);
        soccerFieldContainer.setLayout(soccerFieldContainerLayout);
        soccerFieldContainerLayout.setHorizontalGroup(
            soccerFieldContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 403, Short.MAX_VALUE)
        );
        soccerFieldContainerLayout.setVerticalGroup(
            soccerFieldContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 642, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(soccerFieldContainer);

        org.jdesktop.layout.GroupLayout homeographyMappingPanelLayout = new org.jdesktop.layout.GroupLayout(homeographyMappingPanel);
        homeographyMappingPanel.setLayout(homeographyMappingPanelLayout);
        homeographyMappingPanelLayout.setHorizontalGroup(
            homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homeographyMappingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1)
                    .add(homeographyMappingPanelLayout.createSequentialGroup()
                        .add(homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, homeographyMappingPanelLayout.createSequentialGroup()
                                .add(newPointButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(deletePointButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(calculateButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(mapButton)))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))
                .addContainerGap())
        );
        homeographyMappingPanelLayout.setVerticalGroup(
            homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homeographyMappingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 163, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(newPointButton)
                    .add(deletePointButton)
                    .add(calculateButton)
                    .add(mapButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout videoControlPanelLayout = new org.jdesktop.layout.GroupLayout(videoControlPanel);
        videoControlPanel.setLayout(videoControlPanelLayout);
        videoControlPanelLayout.setHorizontalGroup(
            videoControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );
        videoControlPanelLayout.setVerticalGroup(
            videoControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout phiImagePanelLayout = new org.jdesktop.layout.GroupLayout(phiImagePanel);
        phiImagePanel.setLayout(phiImagePanelLayout);
        phiImagePanelLayout.setHorizontalGroup(
            phiImagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 840, Short.MAX_VALUE)
        );
        phiImagePanelLayout.setVerticalGroup(
            phiImagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 444, Short.MAX_VALUE)
        );

        imageContainerPanel.setMaximumSize(new java.awt.Dimension(836, 498));

        org.jdesktop.layout.GroupLayout imageContainerPanelLayout = new org.jdesktop.layout.GroupLayout(imageContainerPanel);
        imageContainerPanel.setLayout(imageContainerPanelLayout);
        imageContainerPanelLayout.setHorizontalGroup(
            imageContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 980, Short.MAX_VALUE)
        );
        imageContainerPanelLayout.setVerticalGroup(
            imageContainerPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 513, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(imageContainerPanel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(phiImagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 984, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(videoControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(homeographyMappingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(homeographyMappingPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(videoControlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 21, Short.MAX_VALUE)
                        .add(phiImagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deletePointButtonActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePointButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deletePointButtonActionPerformed

    private void newPointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPointButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_newPointButtonActionPerformed

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        homeography = homeographyManager.calculateHomography();
        mapButton.setEnabled(homeography != null);
    }//GEN-LAST:event_calculateButtonActionPerformed

    private void mapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mapButtonActionPerformed
        mappingPoint = true;
    }//GEN-LAST:event_mapButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainApp().run();
                } catch (IOException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void appendHomeographyPair(final Point imagePoint, final Point mappedPoint) {
        homeographyManager.setMapping(imagePoint, mappedPoint);
    }
    private void checkPair() {
        if (this.currentMappedPoint != null && this.currentImagePoint != null) {
            appendHomeographyPair(this.currentImagePoint, this.currentMappedPoint);
            this.currentImagePoint = null;
            this.currentMappedPoint = null;
            selectingPoint = false;
            pointsList.updateUI();
        }
    }
    private void setCurrentSelectedImagePoint(Point point) {
        this.currentImagePoint = point;
        checkPair();
    }
    private void setCurrentSelectedMappedPoint(Point point) {
        this.currentMappedPoint = point;
        checkPair();
    }

    private MainApp run() throws IOException {
        homeographyManager = new HomeographyManager();

        frameDecoder = new FrameDecoder("src/main/resources/WholeField.mov");
        imagePanel = new ImagePanel();
        BufferedImage frame = frameDecoder.nextFrame();
        imagePanel.setSize(frame.getWidth(), frame.getHeight());
        imageContainerPanel.add(imagePanel, CENTER_ALIGNMENT);
        String outFilename = Long.toString(new Date().getTime()) + "-points.txt";
        outFile = new File(outFilename);
        outFile.createNewFile();
        outBuffer = new FileOutputStream(outFile);

        mouseListener = new MouseListener() {

            @Override
            public void mouseReleased(final MouseEvent arg0) {
            }

            @Override
            public void mousePressed(final MouseEvent arg0) {
            }

            @Override
            public void mouseExited(final MouseEvent arg0) {
            }

            @Override
            public void mouseEntered(final MouseEvent arg0) {
            }

            @Override
            public void mouseClicked(final MouseEvent arg0) {
            	if (mappingPoint) {
            		if (homeography != null) {
            			Point mapped = homeography.apply(arg0.getPoint());
            			BufferedImage image = soccerFieldPanel.getImage();

            			if (mapped.x < 0 || mapped.x >= image.getWidth() - 1 || mapped.y < 0 || mapped.y >= image.getHeight()) {
            				System.out.println("Skipping point out of bounds");
            				return;
            			}

            			image.setRGB(mapped.x, mapped.y, Color.red.getRGB());

            			soccerFieldPanel.setImage(image);
            		}
            		mappingPoint = false;
            	} else if (selectingPoint) {
                    setCurrentSelectedImagePoint(arg0.getPoint());
                } else if (selectingFirst) {
                    contour.add(Contour.aroundPoint(selected++, arg0.getPoint()));
                    BufferedImage image = imagePanel.getImage();
                    ImageOperations.drawContourOnBuffer(image, contour.get(contour.size() - 1));
                    imagePanel.setImage(image);
                }
            }

        };
        phiPanel = new ImagePanel();
        phiPanel.setSize(frame.getWidth(), frame.getHeight());
        phiImagePanel.add(phiPanel, CENTER_ALIGNMENT);
 
        imagePanel.addMouseListener(mouseListener);
        startTrackingButton = new Button("Start tracking");
        startTrackingButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                selectingFirst = false;
                videoControlPanel.remove(startTrackingButton);
                startTrackingButton = null;

                ac = new ActiveContour(firstFrame, contour.toArray(new Contour[contour.size()]));
                homeography = homeographyManager.calculateHomography();
                addNextFrameButton();

            }
        });
        videoControlPanel.setLayout(new GridLayout(1, 1));
        videoControlPanel.add(startTrackingButton);

        soccerFieldPanel = new ImagePanel();
        soccerFieldContainer.add(soccerFieldPanel, CENTER_ALIGNMENT);
        
        BufferedImage soccerField = ImageIO.read(new File("src/main/resources/independiente.png"));
        soccerFieldPanel.setImage(soccerField);
        soccerFieldPanel.setSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        soccerFieldPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectingPoint) {
                    setCurrentSelectedMappedPoint(e.getPoint());
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
        
        newPointButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                selectingPoint = true;
            }
        });
        deletePointButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                homeographyManager.removeItem(pointsList.getSelectedIndex());
                pointsList.updateUI();
                deletePointButton.setEnabled(false);
            }
        });
        pointsList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {
                deletePointButton.setEnabled(true);
            }
        });

        pack();
        setVisible(true);

        Dimension oldSize = imageContainerPanel.getPreferredSize();
        
        // Skip first three frames cause it jumps to much
        BufferedImage firstImage = frameDecoder.nextFrame();
        Dimension frameSize = new Dimension(firstImage.getWidth(), firstImage.getHeight());
        imagePanel.setPreferredSize(frameSize);
        imageContainerPanel.setPreferredSize(frameSize);
        
        jScrollPane2.setPreferredSize(oldSize);
        jScrollPane2.setMaximumSize(oldSize);
        imageContainerPanel.revalidate();
        
        frameDecoder.nextFrame();
        frameDecoder.nextFrame();
        pointsList.setModel(homeographyManager.getListModel());
        
        
        Dimension oldSoccerSize = soccerFieldContainer.getPreferredSize();
        oldSoccerSize.height = soccerField.getHeight();
        soccerFieldPanel.setPreferredSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        soccerFieldContainer.setPreferredSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        jScrollPane3.setPreferredSize(oldSoccerSize);
        jScrollPane3.setMaximumSize(oldSoccerSize);
        

        loadNextFrame();
        return this;
    }

    private void addNextFrameButton() {
        Button button = new Button("Next frame");
        button.setSize(new Dimension(100, 10));
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                loadNextFrame();
            }
        });
        videoControlPanel.setLayout(new GridLayout(1, 1));
        videoControlPanel.add(button);

        pack();
        repaint();
    }

    private void loadNextFrame() {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final BufferedImage frame = frameDecoder.nextFrame();
                framesElapsed++;
                if (firstFrame == null) {
                	firstFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType());

                	for (int i = 0; i < frame.getWidth(); i++) {
                		for (int j = 0; j < frame.getHeight(); j++) {
                			firstFrame.setRGB(i, j, frame.getRGB(i, j));
                		}
                	}

                }
                if (ac != null) {
                	BufferedImage phiColor = new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType());
                	int phiMapping[][] = ac.getMapping();
                	for (int x = 0; x < frame.getWidth(); x++) {
                            for (int y = 0; y < frame.getHeight(); y++) {
                	        phiColor.setRGB(x, y, phiColoring[phiMapping[x][y]].getRGB());
                	    }
                	}
                        phiPanel.setImage(phiColor);
                        phiPanel.repaint();
                	BufferedImage coloredFrame = frame.getSubimage(0, 0, frame.getWidth(), frame.getHeight());
                	ac.adapt(coloredFrame);
                	int index = 0;
                	for (Contour c : contour) {
                		ImageOperations.drawContourOnBuffer(coloredFrame, c);

                		if (homeography != null) {
                			BufferedImage image = soccerFieldPanel.getImage();

                			Point mapped = homeography.apply(c.centroidX(), c.maxY());
                			if (mapped.x < 0 || mapped.x >= image.getWidth() - 1 || mapped.y < 0 || mapped.y >= image.getHeight()) {
                				System.out.println("Skipping point out of bounds");
                				continue;
                			}
                			try {
								outBuffer.write(String.format("%d, %d, %d, %d\n", framesElapsed, index++, mapped.x, mapped.y).getBytes());
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
                			image.setRGB(mapped.x, mapped.y, Color.cyan.getRGB());

                			soccerFieldPanel.setImage(image);
                		}
                	}
                	imagePanel.setImage(coloredFrame);
                } else {
                    imagePanel.setImage(frame);
                }
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateButton;
    private javax.swing.JButton deletePointButton;
    private javax.swing.JPanel homeographyMappingPanel;
    private javax.swing.JPanel imageContainerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JButton mapButton;
    private javax.swing.JButton newPointButton;
    private javax.swing.JPanel phiImagePanel;
    private javax.swing.JList pointsList;
    private javax.swing.JPanel soccerFieldContainer;
    private javax.swing.JPanel videoControlPanel;
    // End of variables declaration//GEN-END:variables
}
