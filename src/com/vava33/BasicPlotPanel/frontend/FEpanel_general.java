package com.vava33.BasicPlotPanel.frontend;

/**
 * FrontEnd panel general container (for plot1Dfrontend)
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.vava33.BasicPlotPanel.core.Plot1DFrontend;

import net.miginfocom.swing.MigLayout;

public class FEpanel_general extends JPanel {

    private JCheckBox chckbxShow;
    private JTextField txtXlegend;
    private JCheckBox chckbxAutopos;
    private JCheckBox chckbxOpaque;
    private JTextField txtYlegend;
    Plot1DFrontend fe;
    
    /**
     * Create the panel.
     */
    public FEpanel_general(Plot1DFrontend fe) {
        initGUI();
        this.fe=fe;
    }

    private void initGUI() {
      this.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Legend", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      this.setLayout(new MigLayout("insets 2", "[][grow][grow]", "[][]"));
      
      chckbxShow = new JCheckBox("Show");
      chckbxShow.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
              do_chckbxShow_itemStateChanged(e);
          }
      });
      this.add(chckbxShow, "cell 0 0,alignx left");
      
      txtXlegend = new JTextField();
      txtXlegend.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              do_txtXlegend_actionPerformed(e);
          }
      });
      
      chckbxAutopos = new JCheckBox("Auto");
      chckbxAutopos.setToolTipText("Automatic Position");
      chckbxAutopos.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
              do_chckbxAutopos_itemStateChanged(e);
          }
      });
      
      chckbxOpaque = new JCheckBox("Opaque");
      chckbxOpaque.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
              do_chckbxOpaque_itemStateChanged(e);
          }
      });
      this.add(chckbxOpaque, "cell 1 0 2 1");
      this.add(chckbxAutopos, "cell 0 1,alignx left");
      txtXlegend.setText("Xlegend");
      this.add(txtXlegend, "cell 1 1,growx");
      txtXlegend.setColumns(2);
      
      txtYlegend = new JTextField();
      txtYlegend.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              do_txtYlegend_actionPerformed(e);
          }
      });
      txtYlegend.setText("Ylegend");
      this.add(txtYlegend, "cell 2 1,growx");
      txtYlegend.setColumns(2);
  }
  protected void do_chckbxShow_itemStateChanged(ItemEvent e) {
      // TODO Auto-generated method stub
      
  }
  protected void do_txtXlegend_actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
      
  }
  protected void do_chckbxAutopos_itemStateChanged(ItemEvent e) {
      // TODO Auto-generated method stub
      
  }
  protected void do_txtYlegend_actionPerformed(ActionEvent e) {
      // TODO Auto-generated method stub
      
  }
  protected void do_chckbxOpaque_itemStateChanged(ItemEvent e) {
      // TODO Auto-generated method stub
      
  }
    
}
