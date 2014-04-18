/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.it.itba;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import ar.edu.it.itba.HomeographyManager.Pair;

/**
 *
 * @author eordano
 */
public class PointInFieldDialogue extends javax.swing.JDialog {

	private static final long serialVersionUID = -2482669649186193044L;

	/**
     * Creates new form PointInFieldDialogue
     */
    public PointInFieldDialogue(final java.awt.Frame parent, final boolean modal) {
        super(parent, modal);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jRadioButton13 = new javax.swing.JRadioButton();
        jRadioButton14 = new javax.swing.JRadioButton();
        jRadioButton15 = new javax.swing.JRadioButton();
        jRadioButton16 = new javax.swing.JRadioButton();
        jRadioButton17 = new javax.swing.JRadioButton();
        jRadioButton18 = new javax.swing.JRadioButton();
        jRadioButton19 = new javax.swing.JRadioButton();
        jRadioButton20 = new javax.swing.JRadioButton();
        jRadioButton21 = new javax.swing.JRadioButton();
        jRadioButton22 = new javax.swing.JRadioButton();
        jRadioButton23 = new javax.swing.JRadioButton();
        jRadioButton24 = new javax.swing.JRadioButton();
        jRadioButton25 = new javax.swing.JRadioButton();
        jRadioButton26 = new javax.swing.JRadioButton();
        jRadioButton27 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Where is this point in the field?");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Top left Corner");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Top right Corner");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setText("Bottom left Corner");
        jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton3ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setText("Bottom right Small Area Right Team");
        jRadioButton4.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton4ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setText("Bottom right Big Area Right Team");
        jRadioButton5.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton5ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setText("Bottom right Corner");
        jRadioButton6.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton6ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton7);
        jRadioButton7.setText("Top right Small Area Right Team");
        jRadioButton7.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton7ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton8);
        jRadioButton8.setText("Top right Big Area Right Team");
        jRadioButton8.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton8ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton9);
        jRadioButton9.setText("Bottom left Big Area Right Team");
        jRadioButton9.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton9ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton10);
        jRadioButton10.setText("Top left Big Area Right Team");
        jRadioButton10.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton10ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton11);
        jRadioButton11.setText("Top left Small Area Right Team");
        jRadioButton11.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton11ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton12);
        jRadioButton12.setText("Bottom left Small Area Right Team");
        jRadioButton12.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton12ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton13);
        jRadioButton13.setText("Top left Big Area Left Team");
        jRadioButton13.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton13ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton14);
        jRadioButton14.setText("Bottom left Big Area Left Team");
        jRadioButton14.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton14ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton15);
        jRadioButton15.setText("Top right Big Area Left Team");
        jRadioButton15.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton15ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton16);
        jRadioButton16.setText("Top right Small Area Left Team");
        jRadioButton16.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton16ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton17);
        jRadioButton17.setText("Bottom right Small Area Left Team");
        jRadioButton17.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton17ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton18);
        jRadioButton18.setText("Bottom right Big Area Left Team");
        jRadioButton18.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton18ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton19);
        jRadioButton19.setText("Top left Small Area Left Team");
        jRadioButton19.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton19ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton20);
        jRadioButton20.setText("Bottom left Small Area Left Team");
        jRadioButton20.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton20ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton21);
        jRadioButton21.setText("Bottom center cross");

        buttonGroup1.add(jRadioButton22);
        jRadioButton22.setText("Field Center");

        buttonGroup1.add(jRadioButton23);
        jRadioButton23.setText("Top center cross");

        buttonGroup1.add(jRadioButton24);
        jRadioButton24.setText("Field Center Area Bottom");

        buttonGroup1.add(jRadioButton25);
        jRadioButton25.setText("Field Center Area Top");
        jRadioButton25.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton25ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton26);
        jRadioButton26.setText("Penalty Left Team");

        buttonGroup1.add(jRadioButton27);
        jRadioButton27.setText("Penalty Right Team");
        jRadioButton27.addActionListener(new java.awt.event.ActionListener() {
            @Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
                jRadioButton27ActionPerformed(evt);
            }
        });

        jButton1.setText("Ok");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(421, 421, 421)
                                .add(jRadioButton25))
                            .add(layout.createSequentialGroup()
                                .add(413, 413, 413)
                                .add(jRadioButton24)))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel1)
                                .add(0, 0, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jRadioButton3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jRadioButton21)
                                .add(268, 268, 268)
                                .add(jRadioButton6))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jRadioButton19)
                                            .add(jRadioButton13))
                                        .add(27, 27, 27)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jRadioButton15)
                                            .add(jRadioButton16)))
                                    .add(jRadioButton1))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(355, 355, 355)
                                        .add(jRadioButton2))
                                    .add(layout.createSequentialGroup()
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jRadioButton11)
                                            .add(jRadioButton10))
                                        .add(27, 27, 27)
                                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(jRadioButton8)
                                            .add(jRadioButton7)))))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jRadioButton26)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jRadioButton20)
                                        .add(jRadioButton14)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jRadioButton17)
                                    .add(jRadioButton18))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButton12)
                                    .add(jRadioButton9))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jRadioButton4)
                                    .add(jRadioButton5))))))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 0, Short.MAX_VALUE)
                .add(jRadioButton23)
                .add(433, 433, 433))
            .add(layout.createSequentialGroup()
                .add(449, 449, 449)
                .add(jRadioButton22)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jRadioButton27)
                .add(95, 95, 95))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButton1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(jRadioButton23)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton2)
                    .add(jRadioButton1))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton15)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton16))
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton10)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton11))
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton13)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton19))
                    .add(layout.createSequentialGroup()
                        .add(jRadioButton8)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jRadioButton7)))
                .add(18, 18, 18)
                .add(jRadioButton25)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton22)
                    .add(jRadioButton26)
                    .add(jRadioButton27))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton24)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jRadioButton4)
                            .add(jRadioButton17))
                        .add(29, 29, 29))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jRadioButton12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRadioButton9)
                            .add(jRadioButton5)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jRadioButton20)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jRadioButton14)
                            .add(jRadioButton18))))
                .add(26, 26, 26)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton6)
                    .add(jRadioButton3)
                    .add(jRadioButton21))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, Short.MAX_VALUE)
                .add(jButton1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton2ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jRadioButton3ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton3ActionPerformed

    private void jRadioButton4ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton4ActionPerformed

    private void jRadioButton5ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton5ActionPerformed

    private void jRadioButton6ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton6ActionPerformed

    private void jRadioButton7ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton7ActionPerformed

    private void jRadioButton8ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton8ActionPerformed

    private void jRadioButton9ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton9ActionPerformed

    private void jRadioButton10ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton10ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton10ActionPerformed

    private void jRadioButton11ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton11ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton11ActionPerformed

    private void jRadioButton12ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton12ActionPerformed

    private void jRadioButton13ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton13ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton13ActionPerformed

    private void jRadioButton14ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton14ActionPerformed

    private void jRadioButton15ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton15ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton15ActionPerformed

    private void jRadioButton16ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton16ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton16ActionPerformed

    private void jRadioButton17ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton17ActionPerformed

    private void jRadioButton18ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton18ActionPerformed

    private void jRadioButton19ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton19ActionPerformed

    private void jRadioButton20ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton20ActionPerformed

    private void jRadioButton1ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton25ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton25ActionPerformed

    private void jRadioButton27ActionPerformed(final java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton27ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PointInFieldDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PointInFieldDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PointInFieldDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PointInFieldDialogue.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
			public void run() {
                PointInFieldDialogue dialog = new PointInFieldDialogue(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(final java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    public static interface PointInFieldListener {
        public void getResult(Pair p);
    }

    public static void showUp(final JFrame parent, final Point p, final int width, final int depth, final PointInFieldListener listener) {
        final PointInFieldDialogue d = new PointInFieldDialogue(parent, true);
        d.jButton1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                Point result = new Point();
                JRadioButton j = null;
                Enumeration<AbstractButton> elements = d.buttonGroup1.getElements();
                while (elements.hasMoreElements()) {
                    JRadioButton button = (JRadioButton) elements.nextElement();
                    if (button.isSelected()) {
                        j = button;
                    }
                }
                if (j != null) {
                    String t = j.getText();
                    int middleDepth = depth / 2;
                    int middleWidth = width / 2;
                    int circleArea = 915;
                    int penaltyDistance = 1100;
                    int bigAreaDepth = 1650;
                    int smallAreaDepth = 550;
                    int bigAreaWidth = 2015;
                    int smallAreaWidth = 916;
                    if (t.equals("Top left Corner")) {
                        result.x = 0;
                        result.y = width;
                    } else if (t.equals("Top right Corner")) {
                        result.x = depth;
                        result.y = width;
                    } else if (t.equals("Bottom left Corner")) {
                        result.x = 0;
                        result.y = 0;
                    } else if (t.equals("Bottom right Corner")) {
                        result.x = depth;
                        result.y = 0;
                    } else if (t.equals("Top center cross")) {
                        result.x = middleDepth;
                        result.y = width;
                    } else if (t.equals("Bottom center cross")) {
                        result.x = middleDepth;
                        result.y = 0;
                    } else if (t.equals("Field Center")) {
                        result.x = middleDepth;
                        result.y = middleWidth;
                    } else if (t.equals("Field Center Area Top")) {
                        result.x = middleDepth;
                        result.y = middleWidth + circleArea;
                    } else if (t.equals("Field Center Area Bottom")) {
                        result.x = middleDepth;
                        result.y = middleWidth + circleArea;
                    } else if (t.equals("Top left Big Area Left Team")) {
                        result.x = 0;
                        result.y = middleWidth + bigAreaWidth;
                    } else if (t.equals("Top left Small Area Left Team")) {
                        result.x = 0;
                        result.y = middleWidth + smallAreaWidth;
                    } else if (t.equals("Bottom left Big Area Left Team")) {
                        result.x = 0;
                        result.y = middleWidth - bigAreaWidth;
                    } else if (t.equals("Bottom left Small Area Left Team")) {
                        result.x = 0;
                        result.y = middleWidth - smallAreaWidth;
                    }else if (t.equals("Top right Big Area Left Team")) {
                        result.x = bigAreaDepth;
                        result.y = middleWidth + bigAreaWidth;
                    } else if (t.equals("Top right Small Area Left Team")) {
                        result.x = smallAreaDepth;
                        result.y = middleWidth + smallAreaWidth;
                    } else if (t.equals("Bottom right Big Area Left Team")) {
                        result.x = bigAreaDepth;
                        result.y = middleWidth - bigAreaWidth;
                    } else if (t.equals("Bottom right Small Area Left Team")) {
                        result.x = smallAreaDepth;
                        result.y = middleWidth - smallAreaWidth;
                    } else if (t.equals("Penalty Left Team")) {
                        result.x = penaltyDistance;
                        result.y = middleWidth;
                    } else if (t.equals("Top right Big Area Right Team")) {
                        result.x = depth;
                        result.y = middleWidth + bigAreaWidth;
                    } else if (t.equals("Top right Small Area Right Team")) {
                        result.x = depth;
                        result.y = middleWidth + smallAreaWidth;
                    } else if (t.equals("Bottom right Big Area Right Team")) {
                        result.x = depth;
                        result.y = middleWidth - bigAreaWidth;
                    } else if (t.equals("Bottom right Small Area Right Team")) {
                        result.x = depth;
                        result.y = middleWidth - smallAreaWidth;
                    } else if (t.equals("Top left Big Area Right Team")) {
                        result.x = depth - bigAreaDepth;
                        result.y = middleWidth + bigAreaWidth;
                    } else if (t.equals("Top left Small Area Right Team")) {
                        result.x = depth - smallAreaDepth;
                        result.y = middleWidth + smallAreaWidth;
                    } else if (t.equals("Bottom left Big Area Right Team")) {
                        result.x = depth - bigAreaDepth;
                        result.y = middleWidth - bigAreaWidth;
                    } else if (t.equals("Bottom left Small Area Right Team")) {
                        result.x = depth - smallAreaDepth;
                        result.y = middleWidth - smallAreaWidth;
                    } else if (t.equals("Penalty Right Team")) {
                        result.x = depth - penaltyDistance;
                        result.y = middleWidth;
                    } else {
                        System.err.println("COULDN'T GET POINT");
                    }
                    listener.getResult(new Pair(p, result));
                }
                d.dispose();
            }
        });
        d.setVisible(true);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton13;
    private javax.swing.JRadioButton jRadioButton14;
    private javax.swing.JRadioButton jRadioButton15;
    private javax.swing.JRadioButton jRadioButton16;
    private javax.swing.JRadioButton jRadioButton17;
    private javax.swing.JRadioButton jRadioButton18;
    private javax.swing.JRadioButton jRadioButton19;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton20;
    private javax.swing.JRadioButton jRadioButton21;
    private javax.swing.JRadioButton jRadioButton22;
    private javax.swing.JRadioButton jRadioButton23;
    private javax.swing.JRadioButton jRadioButton24;
    private javax.swing.JRadioButton jRadioButton25;
    private javax.swing.JRadioButton jRadioButton26;
    private javax.swing.JRadioButton jRadioButton27;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    // End of variables declaration//GEN-END:variables
}
