/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ar.edu.it.itba.metrics.HeatMap;
import ar.edu.it.itba.processing.ActiveContour;
import ar.edu.it.itba.processing.Contour;
import ar.edu.it.itba.processing.Homography;
import ar.edu.it.itba.processing.PlayerContour;
import ar.edu.it.itba.processing.color.ColorPoint;
import ar.edu.it.itba.video.BlackOutOutskirts;
import ar.edu.it.itba.video.FrameDecoder;
import ar.edu.it.itba.video.FrameProvider;
import ar.edu.it.itba.video.HoughLines;
import ar.edu.it.itba.video.LensCorrection;

/**
 *
 * @author eordano
 */
public class MainApp extends javax.swing.JFrame {

    private static final long serialVersionUID = 5474244925067694842L;

    private Point currentImagePoint;
    private Point currentMappedPoint;
    private BufferedImage soccerField;
    private boolean reselectingPlayer = false;

    /**
     * Creates new form MainApp
     */
    public MainApp() {
        initComponents();
    }

    private ImagePanel imagePanel;
    private ImagePanel soccerFieldPanel;
    private FrameProvider frameDecoder;

    private File outFile;
    private OutputStream outBuffer;
    private ActiveContour ac;
    private ActiveContour invertedTracker;

    private final List<PlayerContour> contour = new ArrayList<PlayerContour>();

    private MouseListener mouseListener;
    private HomeographyManager homeographyManager;
    private int selected = 1;
    int framesElapsed = 0;

    // Multithread automatic playing
	private boolean playing;
	private Thread playThread;
	private CountDownLatch busyLock;

    private boolean selectingFirst = true;
    private boolean selectingPoint = false;
    private Button startTrackingButton;
    protected Homography homeography;
    protected boolean mappingPoint;
    private BufferedImage frame;
    protected boolean selectRectangle = true;
    private Button shapeButton;
    private Button typeButton;
    protected ColorPoint.Type type = ColorPoint.Type.RGB;

    protected HeatMap heatMap;

	protected int selectedContour = -1;

	protected BufferedImage firstFrame;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings({ "unchecked", "rawtypes", "serial" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        homeographyMappingPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        playerList = new javax.swing.JList();
        jButton2 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        soccerFieldContainer = new javax.swing.JPanel();
        videoControlPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        imageContainerPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Jugadores");

        jButton1.setText("Ver Estadisticas");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        playerList.setToolTipText("");
        jScrollPane1.setViewportView(playerList);

        jButton2.setText("Corregir posición");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jScrollPane3.setMaximumSize(new java.awt.Dimension(403, 551));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(403, 551));

        org.jdesktop.layout.GroupLayout soccerFieldContainerLayout = new org.jdesktop.layout.GroupLayout(soccerFieldContainer);
        soccerFieldContainer.setLayout(soccerFieldContainerLayout);
        soccerFieldContainerLayout.setHorizontalGroup(
            soccerFieldContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 511, Short.MAX_VALUE)
        );
        soccerFieldContainerLayout.setVerticalGroup(
            soccerFieldContainerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 815, Short.MAX_VALUE)
        );

        jScrollPane3.setViewportView(soccerFieldContainer);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jButton1)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton2))
                            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 463, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 6, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 156, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1)
                    .add(jButton2))
                .add(41, 41, 41)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 302, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout videoControlPanelLayout = new org.jdesktop.layout.GroupLayout(videoControlPanel);
        videoControlPanel.setLayout(videoControlPanelLayout);
        videoControlPanelLayout.setHorizontalGroup(
            videoControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 469, Short.MAX_VALUE)
        );
        videoControlPanelLayout.setVerticalGroup(
            videoControlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 163, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout homeographyMappingPanelLayout = new org.jdesktop.layout.GroupLayout(homeographyMappingPanel);
        homeographyMappingPanel.setLayout(homeographyMappingPanelLayout);
        homeographyMappingPanelLayout.setHorizontalGroup(
            homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, homeographyMappingPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(videoControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(63, 63, 63))
        );
        homeographyMappingPanelLayout.setVerticalGroup(
            homeographyMappingPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(homeographyMappingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(videoControlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 907, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(homeographyMappingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 481, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(homeographyMappingPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (playerList.isSelectionEmpty()) {
            return;
        }
        PlayerContour c = contour.get(playerList.getSelectedIndex());
        new PlayerStatsDialog(this, false, c).setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (playerList.getSelectedIndex() != -1) {
            jButton2.setText("Click in player on field");
            jButton2.setEnabled(false);
            reselectingPlayer = true;
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
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
        }
    }
    private void setCurrentSelectedImagePoint(final Point point) {
        this.currentImagePoint = point;
        checkPair();
    }
    private void setCurrentSelectedMappedPoint(final Point point) {
        this.currentMappedPoint = point;
        checkPair();
    }

    private MainApp run() throws IOException {
        homeographyManager = new HomeographyManager();

        //frameDecoder = new BackgroundDetection(new FrameDecoder("/Users/jpcivile/Desktop/Boca1.mp4"), 60);
        //frameDecoder = new BackgroundDetection(new FrameDecoder("/home/acrespo/Dropbox/ati-2013/Independiente2b.mp4"), 60);
        //frameDecoder = new FrameDecoder("/Users/eordano/Desktop/A1.mp4");
        //frameDecoder = new FrameDecoder("/Users/eordano/Downloads/Boca1.mp4");
        // frameDecoder = new LensCorrection(new FrameDecoder("/Users/jpcivile/Desktop/Boca1.mp4"), 1.6175);
        List<Point> points = new LinkedList<Point>();
        points.add(new Point(425, 40));
        points.add(new Point(54, 321));
        points.add(new Point(1518, 345));
        points.add(new Point(1147, 52));
        List<Point> firstPoints = new LinkedList<Point>();
        firstPoints.add(new Point(200, 375));
        firstPoints.add(new Point(200, 750));
        firstPoints.add(new Point(1750, 750));
        firstPoints.add(new Point(1750, 375));
        homeographyManager.setMapping(new Point(370, -2), new Point(16, 5));
        homeographyManager.setMapping(new Point(-2, 285), new Point(16, 282));
        homeographyManager.setMapping(new Point(1460, 305), new Point(441, 282));
        homeographyManager.setMapping(new Point(1091, 13), new Point(441,5));
        frameDecoder = // new BackgroundDetection(
                new BlackOutOutskirts(
                new LensCorrection(
                        new BlackOutOutskirts(
//                        new FrameDecoder("/Users/eordano/Downloads/Boca1.mp4")
                        new FrameDecoder("/Users/jpcivile/Documents/ITBA/final/Boca1.mp4")
               , firstPoints)
                , 1.91)
        , points);

        // First frame + Hough lines
        imagePanel = new ImagePanel();
        frameDecoder.nextFrame();
        BufferedImage frame = buildImage();

        frameDecoder = new HoughLines(frameDecoder, frame);

        imagePanel.setSize(frame.getWidth(), frame.getHeight());
        imageContainerPanel.add(imagePanel, CENTER_ALIGNMENT);
        String outFilename = Long.toString(new Date().getTime()) + "-points.txt";
        outFile = new File(outFilename);
        outFile.createNewFile();
        outBuffer = new FileOutputStream(outFile);


        playerList.setModel(new AbstractListModel() {

            @Override
            public int getSize() {
                return contour.size();
            }

            @Override
            public Object getElementAt(final int index) {
                return contour.get(index).description();
            }

        });
        playerList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(final ListSelectionEvent e) {

            	if (selectedContour != -1) {
                    ImageOperations.drawContourOnBuffer(imagePanel.getImage(), contour.get(selectedContour));
                    imagePanel.repaint();
            	}

                selectedContour = playerList.getSelectedIndex();
                if (selectedContour >= 0 && selectedContour < contour.size()) {
                    soccerFieldPanel.setImage(contour.get(selectedContour).getHeatMap().getFrame());
                    soccerFieldPanel.repaint();
                    ImageOperations.paintContour(imagePanel.getImage(), contour.get(selectedContour), Color.RED);
                    imagePanel.repaint();
                }
            }

        });
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
                if (reselectingPlayer) {
                    int index = playerList.getSelectedIndex();
                    jButton2.setEnabled(true);
                    jButton2.setText("Corregir posición");
                    reselectingPlayer = false;
                    Rectangle rectangle;
                    if (selectRectangle) {
                        rectangle = new Rectangle(arg0.getPoint().x - 3, arg0.getPoint().y - 6, 6, 12);
                    } else {
                        rectangle = new Rectangle(arg0.getPoint().x - 3, arg0.getPoint().y - 3, 6, 6);
                    }

                    BufferedImage image = imagePanel.getImage();
                    ac.resetContourToRect(image, contour.get(index), rectangle);

                    ImageOperations.drawContourOnBuffer(image, contour.get(index));
                    imagePanel.setImage(image);

                    playerList.updateUI();
                } else {
                    new SelectPlayer(MainApp.this, true, new PlayerSelectionListener(){

                        @Override
                        public void selectedPlayer(final String name, final String position, final String team) {
                            if (selectingFirst) {
                                addPlayer(arg0.getPoint(), name, position, team);
                            } else {
                                BufferedImage image = addPlayer(arg0.getPoint(), name, position, team);
                                if (ac != null) {
                                    ac = new ActiveContour(image, contour.toArray(new Contour[contour.size()]));
                                }
                            }
                            playerList.updateUI();
                        }

                    }).setVisible(true);
                }
            }

        };

        shapeButton = new Button("Rectangle");
        shapeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
            	selectRectangle = !selectRectangle;

            	if (selectRectangle) {
            		shapeButton.setLabel("Rectangle");
            	} else {
            		shapeButton.setLabel("Square");
            	}
            }
        });
        typeButton = new Button("Type = RGB");
        typeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
            	switch (type) {
				case HS:
					type = ColorPoint.Type.RGB;
					typeButton.setLabel("Type = RGB");
					break;
				case HSI:
					type = ColorPoint.Type.HS;
					typeButton.setLabel("Type = HS");
					break;
				case RGB:
					type = ColorPoint.Type.HSI;
					typeButton.setLabel("Type = HSI");
					break;
				default:
					break;
            	}
            }
        });

        imagePanel.addMouseListener(mouseListener);
        startTrackingButton = new Button("Start tracking");
        startTrackingButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent arg0) {
                selectingFirst = false;
                videoControlPanel.remove(startTrackingButton);
                startTrackingButton = null;

                List<Contour> team1 = new ArrayList<Contour>();
                List<Contour> team2 = new ArrayList<Contour>();

                for (PlayerContour c : contour) {
					if ("Visitante".equals(c.team)) {
						team2.add(c);
				 	} else {
				 		team1.add(c);
				 	}
				}

                ac = new ActiveContour(firstFrame, team1.toArray(new Contour[team1.size()]));
                ac.setInvertedDetection(true);

                invertedTracker = new ActiveContour(firstFrame, team2.toArray(new Contour[team2.size()]));
                invertedTracker.setInvertedDetection(false);

                for (Contour c : team2) {
                	c.setLastStdDev(ColorPoint.buildFromRGB(ColorPoint.Type.RGB, 0));
                	c.omega = new ColorPoint[] {
                		ColorPoint.buildFromRGB(ColorPoint.Type.RGB, Color.CYAN.getRGB())
                	};
				}

                homeography = homeographyManager.calculateHomography();
                heatMap = new HeatMap(soccerFieldPanel.getImage());
//                homeography = homeographyManager.calculateIterativeHomegraphy(1);
                addNextFrameButton();

            }
        });
        videoControlPanel.setLayout(new GridLayout(1, 1));
        videoControlPanel.add(startTrackingButton);

        soccerFieldPanel = new ImagePanel();
        soccerFieldContainer.add(soccerFieldPanel, CENTER_ALIGNMENT);
        soccerField = ImageIO.read(new File("src/main/resources/independiente.png"));
        soccerFieldPanel.setImage(soccerField);
        soccerFieldPanel.setSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        soccerFieldPanel.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseMoved(final MouseEvent arg0) {
                    if (homeography != null) {
                        Point inverseApply = homeography.inverseApply(arg0.getX(), arg0.getY());
                        if (inverseApply.x > 0 && inverseApply.x < getFrame().getWidth()
                                && inverseApply.y > 0 && inverseApply.y < getFrame().getHeight()) {
                            getFrame().setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                            imagePanel.setImage(getFrame());
                        }
                    }
                }

                @Override
                public void mouseDragged(final MouseEvent arg0) {
                }
            }
        );
        soccerFieldPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(final MouseEvent e) {
                if (selectingPoint) {
                    setCurrentSelectedMappedPoint(e.getPoint());
                }
            }

            @Override
            public void mousePressed(final MouseEvent e) {
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
            }

            @Override
            public void mouseExited(final MouseEvent e) {
            }

        });
        pack();
        repaint();
        setVisible(true);

        Dimension oldSize = imageContainerPanel.getPreferredSize();

        // Skip first three frames cause it jumps to much
        frameDecoder.nextFrame();
        BufferedImage firstImage = buildImage();
        Dimension frameSize = new Dimension(firstImage.getWidth(), firstImage.getHeight());
        imagePanel.setPreferredSize(frameSize);
        imageContainerPanel.setPreferredSize(frameSize);

        jScrollPane2.setPreferredSize(oldSize);
        jScrollPane2.setMaximumSize(oldSize);
        imageContainerPanel.revalidate();

        // for (int i = 0; i < 8; i++) {
        //	frameDecoder.nextFrame();
        // }
        frameDecoder.nextFrame();

        Dimension oldSoccerSize = soccerFieldContainer.getPreferredSize();
        oldSoccerSize.height = soccerField.getHeight();
        soccerFieldPanel.setPreferredSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        soccerFieldContainer.setPreferredSize(new Dimension(soccerField.getWidth(), soccerField.getHeight()));
        jScrollPane3.setPreferredSize(oldSoccerSize);
        jScrollPane3.setMaximumSize(oldSoccerSize);

        try {
			loadNextFrame().await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        imagePanel.setImage(getFrame());

        addPlayer(new Point(347,92), "Arquero", "1", "Local");
        addPlayer(new Point(547,121), "Defensor 1", "2", "Local");
        addPlayer(new Point(566,169), "Defensor 2", "3", "Local");
        addPlayer(new Point(560,73), "Defensor 3", "4", "Local");
        addPlayer(new Point(632,38), "Defensor 4", "5", "Local");
        addPlayer(new Point(687,90), "Mediocampista 1", "6", "Local");
        addPlayer(new Point(709,150), "Mediocampista 2", "7", "Local");
        addPlayer(new Point(780,67), "Mediocampista 3", "8", "Local");
        addPlayer(new Point(791,50), "Delantero 1", "9", "Local");
        addPlayer(new Point(806,114), "Delantero 2", "10", "Local");
        addPlayer(new Point(762,180), "Delantero 3", "11", "Local");
        addPlayer(new Point(1078,88), "Arquero", "1", "Visitante");
        addPlayer(new Point(974,146), "Defensor 1", "2", "Visitante");
        addPlayer(new Point(913,78), "Defensor 2", "3", "Visitante");
        addPlayer(new Point(863,45), "Defensor 3", "4", "Visitante");
        addPlayer(new Point(891,211), "Defensor 4", "5", "Visitante");
        addPlayer(new Point(775,148), "Mediocampista 1", "6", "Visitante");
        addPlayer(new Point(789,102), "Mediocampista 2", "7", "Visitante");
        addPlayer(new Point(690,36), "Mediocampista 3", "8", "Visitante");
        addPlayer(new Point(629,79), "Delantero 1", "9", "Visitante");
        addPlayer(new Point(568,75), "Delantero 2", "10", "Visitante");
        addPlayer(new Point(558,122), "Delantero 3", "11", "Visitante");
        addPlayer(new Point(673,100), "Arbitro", "-", "Arbitro");
        addPlayer(new Point(1053,290), "Juez de línea", "-", "Arbitro");
        playerList.updateUI();
        return this;
    }


	private BufferedImage addPlayer(final Point point,
			final String name, final String position,
			final String team) {
		PlayerContour c;
		if (selectRectangle) {
		    c = PlayerContour.aroundPoint(name, position, team, selected++, point);
		} else {
		    c = PlayerContour.squareAroundPoint(name, position, team, selected++, point);
		}
		c.setType(type);
		// System.out.println("addPlayer(new Point(" + point.x + "," + point.y + "), \"" + name + "\", \"" + position + "\", \"" + team + "\");");

		contour.add(c);
		c.setHeatMap(new HeatMap(soccerField));
		BufferedImage image = imagePanel.getImage();
		ImageOperations.drawContourOnBuffer(image, contour.get(contour.size() - 1));
		imagePanel.setImage(image);
		return image;
	}

    private BufferedImage buildImage() {
    	BufferedImage image = new BufferedImage(frameDecoder.getWidth(), frameDecoder.getHeight(), frameDecoder.getType());
    	for (int i = 0; i < frameDecoder.getWidth(); i++) {
    		for (int j = 0; j < frameDecoder.getHeight(); j++) {
    			image.setRGB(i, j, frameDecoder.getRGB(i,  j));
    		}
    	}
		return image;
	}

	protected BufferedImage getFrame() {
    	return frame;
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
        videoControlPanel.setLayout(new GridLayout(2, 1));
        videoControlPanel.add(button);

			final Button button2 = new Button("Play");
			button2.setSize(new Dimension(100, 10));
			final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						busyLock = loadNextFrame();
						waitForBusyLock();
						repaint();
					}
				} catch (InterruptedException e) {
					return;
				}
			}
		};
		final ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {
				if (!playing) {
					playThread = new Thread(runnable);
					playThread.start();
					playing = true;
					button2.setLabel("Pause");
				} else {
					if (playThread != null) {
						playThread.interrupt();
						playing = false;
						button2.setLabel("Play");
					}
				}
			}
		};
		button2.addActionListener(actionListener);
		videoControlPanel.add(button2);

        pack();
        repaint();
    }

	private void waitForBusyLock() throws InterruptedException {
		busyLock.await();
	}

    private CountDownLatch loadNextFrame() {

    	final CountDownLatch busyLock = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
            	long time = System.currentTimeMillis();
            	frameDecoder.nextFrame();
                frame = MainApp.this.buildImage();
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
                    /*
                     BufferedImage phiColor = new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType());
                     int phiMapping[][] = ac.getMapping();
                     for (int x = 0; x < frame.getWidth(); x++) {
                        for (int y = 0; y < frame.getHeight(); y++) {
                            phiColor.setRGB(x, y, phiColoring[phiMapping[x][y]].getRGB());
                        }
                     }
                     phiPanel.setImage(phiColor);
                     phiPanel.repaint();
                     */
                    ac.adapt(frame);
                    invertedTracker.adapt(frame);

                    int index = 0;
                    if (homeography != null) {
                        BufferedImage cancha = soccerField;
                        for (int i = 0; i < cancha.getWidth(); i++) {
                            for (int j = 0; j < cancha.getHeight(); j++) {
                                if (cancha.getRGB(i, j) == Color.black.getRGB()) {
                                    Point inverseApply = homeography.inverseApply(i, j);
                                    if (inverseApply.x > 0 && inverseApply.x < getFrame().getWidth()
                                            && inverseApply.y > 0 && inverseApply.y < getFrame().getHeight()) {
                                        // getFrame().setRGB(inverseApply.x, inverseApply.y, Color.magenta.getRGB());
                                        // imagePanel.setImage(getFrame());
                                    }
                                }
                            }
                        }
                    }
                    for (Contour c : contour) {
                        ImageOperations.drawContourOnBuffer(frame, c);

                        if (homeography != null) {
                            final BufferedImage image = soccerField;

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
//                			image.setRGB(mapped.x, mapped.y, Color.cyan.getRGB());

                            heatMap.addPoint(mapped);

                            ((PlayerContour) c).addHistoricalPoint(mapped);

//                			setSoccerFieldImage(image);
                        }
                    }

                    if (!playerList.isSelectionEmpty()) {
                        ImageOperations.paintContour(imagePanel.getImage(), contour.get(playerList.getSelectedIndex()), Color.RED);
                    }

                    if (heatMap != null) {
	                    soccerFieldPanel.setImage(heatMap.getFrame());
	                    soccerFieldPanel.repaint();
                    }

                    setImagePanelImage(frame);
                } else {
                    setImagePanelImage(frame);
                }
                System.out.println("Frame processed in " + (System.currentTimeMillis() - time) + " ms");
                busyLock.countDown();
            }
        }).start();
        return busyLock;
    }


	private void setImagePanelImage(final BufferedImage frame) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
		@Override
                public void run() {
                    imagePanel.setImage(frame);
                    imagePanel.repaint();
                }
            });
	}

	private void setSoccerFieldImage(final BufferedImage image) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    soccerFieldPanel.setImage(image);
                }
            });
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel homeographyMappingPanel;
    private javax.swing.JPanel imageContainerPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList playerList;
    private javax.swing.JPanel soccerFieldContainer;
    private javax.swing.JPanel videoControlPanel;
    // End of variables declaration//GEN-END:variables
}
