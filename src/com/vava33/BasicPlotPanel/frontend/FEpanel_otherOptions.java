package com.vava33.BasicPlotPanel.frontend;

/**
 * 
 * @author Oriol Vallcorba
 * Licence: GPLv3
 * 
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.vava33.BasicPlotPanel.OptionsDialog;
import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.jutils.Options;

import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;

public class FEpanel_otherOptions extends JPanel {

    private static final long serialVersionUID = 1L;
    Plot1DFrontend fe;
    public Plot1DPanel<?> graphPanel;
    private JCheckBox chckbxAddYbkg;
    private JCheckBox chckbxHklInfo;
    private JCheckBox chckbxRefIntensity;
    private JCheckBox chckbxSplitYref;
    private JCheckBox chckbxShowPointLabels;
    private JCheckBox chckbxShowSerieName;
    private JTextField txtYoffName;
    private JTextField txtXoffName;
    private JButton btnMore;
    OptionsDialog optDialog;
    
    public FEpanel_otherOptions() {
        initGUI();
    }

    public void setFrontend(Plot1DFrontend fe) {
        this.fe=fe;
        graphPanel = fe.getGraphPanel();
    }
    
    private void initGUI() {
        this.setBorder(new TitledBorder(null, "Other options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new MigLayout("", "[grow][grow]", "[][][][][][]"));
        
        
                chckbxShowPointLabels = new JCheckBox("Point labels");
                chckbxShowPointLabels.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        do_chckbxShowPointLabels_itemStateChanged(e);
                    }
                });
                

                this.add(chckbxShowPointLabels, "cell 0 0");
        
        chckbxShowSerieName = new JCheckBox("Series Name, offset (x,y):");
        chckbxShowSerieName.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShowSerieName_itemStateChanged(e);
            }
        });
        
        chckbxRefIntensity = new JCheckBox("REF intensity");
        chckbxRefIntensity.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxRefIntensity_itemStateChanged(e);
            }
        });
        
        chckbxAddYbkg = new JCheckBox("Add Ybkg");
        chckbxAddYbkg.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxAddYbkg_itemStateChanged(e);
            }
        });
        this.add(chckbxAddYbkg, "cell 1 0");
        
        chckbxHklInfo = new JCheckBox("REF labels");
        chckbxHklInfo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxHklInfo_itemStateChanged(e);
            }
        });
        this.add(chckbxHklInfo, "cell 0 1");
        
        chckbxSplitYref = new JCheckBox("Split Yref");
        chckbxSplitYref.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxSplitYref_itemStateChanged(e);
            }
        });
        this.add(chckbxSplitYref, "cell 1 1");
        this.add(chckbxRefIntensity, "cell 0 2 2 1");
        this.add(chckbxShowSerieName, "cell 0 3 2 1");
        
        txtYoffName = new JTextField();
        txtYoffName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYoffName_actionPerformed(e);
            }
        });
        
        txtXoffName = new JTextField();
        txtXoffName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXoffName_actionPerformed(e);
            }
        });
        this.add(txtXoffName, "cell 0 4,growx");
        txtXoffName.setColumns(2);
        this.add(txtYoffName, "cell 1 4,growx");
        txtYoffName.setColumns(2);
        
        btnMore = new JButton("more...");
        btnMore.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_btnMore_actionPerformed(e);
            }
        });
        add(btnMore, "cell 0 5 2 1,alignx center");
    }
    
  public void inicia() {
      this.chckbxHklInfo.setSelected(graphPanel.isHkllabels());
      this.chckbxShowPointLabels.setSelected(graphPanel.isShowPointLabels());
      this.chckbxShowSerieName.setSelected(graphPanel.isShowSerieNameInGraph());
      this.txtYoffName.setText(Integer.toString(graphPanel.getSerieNameOffsetPixY()));
      this.txtXoffName.setText(Integer.toString(graphPanel.getSerieNameOffsetPixX()));
  }
  
  //AQUEST HKLINFO ES L'UNIC QUE NO AFECTA EL DRAWING...
  protected void do_chckbxHklInfo_itemStateChanged(ItemEvent e) {
      graphPanel.setHkllabels(chckbxHklInfo.isSelected());
      graphPanel.actualitzaPlot();
  }
    
  private void do_chckbxAddYbkg_itemStateChanged(ItemEvent e) {
      graphPanel.setPlotwithbkg(chckbxAddYbkg.isSelected());
      graphPanel.actualitzaPlot();
  }

  private void do_chckbxRefIntensity_itemStateChanged(ItemEvent e) {
      graphPanel.setShowDBCompoundIntensity(chckbxRefIntensity.isSelected());
      graphPanel.actualitzaPlot();
  }
  private void do_chckbxSplitYref_itemStateChanged(ItemEvent e) {
      graphPanel.setSplitDBCompound(chckbxSplitYref.isSelected());
      graphPanel.actualitzaPlot();
  }
  
  private void do_chckbxShowPointLabels_itemStateChanged(ItemEvent e) {
      graphPanel.setShowPointLabels(chckbxShowPointLabels.isSelected());
      graphPanel.actualitzaPlot();
  }
  private void do_chckbxShowSerieName_itemStateChanged(ItemEvent e) {
      graphPanel.setShowSerieNameInGraph(chckbxShowSerieName.isSelected());
      graphPanel.actualitzaPlot();
  }

  private void do_txtYoffName_actionPerformed(ActionEvent e) {
      if (this.chckbxShowSerieName.isSelected()) {
          int off=0;
          try {
              off = Integer.parseInt(txtYoffName.getText());
          }catch(NumberFormatException ex) {
              fe.getLog().info("Invalid offset, introduce an integer number");
          }
          graphPanel.setSerieNameOffsetPixY(off);
          graphPanel.actualitzaPlot();
      }
  }
  
  
  protected void do_txtXoffName_actionPerformed(ActionEvent e) {
      if (this.chckbxShowSerieName.isSelected()) {
          int off=0;
          try {
              off = Integer.parseInt(txtXoffName.getText());
          }catch(NumberFormatException ex) {
              fe.getLog().info("Invalid offset, introduce an integer number");
          }
          graphPanel.setSerieNameOffsetPixX(off);
          graphPanel.actualitzaPlot();
      }
  }
    
  public void readOptions(Options opt) {
      this.chckbxHklInfo.setSelected(opt.getValAsBoolean("hkl", this.chckbxHklInfo.isSelected()));
      this.chckbxShowPointLabels.setSelected(opt.getValAsBoolean("pointLabels", this.chckbxShowPointLabels.isSelected()));
      this.txtXoffName.setText(opt.getValAsString("serieNameInGraphOffX", Integer.toString(graphPanel.getSerieNameOffsetPixX())));
      this.txtYoffName.setText(opt.getValAsString("serieNameInGraphOffY", Integer.toString(graphPanel.getSerieNameOffsetPixY())));
      this.chckbxShowSerieName.setSelected(opt.getValAsBoolean("serieNameInGraph", this.chckbxShowSerieName.isSelected()));
  }

    protected void do_btnMore_actionPerformed(ActionEvent e) {
        if (optDialog==null) {
            optDialog = new OptionsDialog(fe);
        }
        optDialog.setVisible(true);
    }
}
