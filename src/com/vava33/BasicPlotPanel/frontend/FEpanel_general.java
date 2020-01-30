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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.vava33.BasicPlotPanel.BasicSerie;
import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.BasicPlotPanel.core.SerieType;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.Options;

import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class FEpanel_general extends JPanel {

    private static final long serialVersionUID = 1L;
    Plot1DFrontend fe;
    public Plot1DPanel<?> graphPanel;
    private JLabel lblXTitle;
    private JLabel lblYTitle;
    private JLabel lblTheme;
    private JLabel lblTitle;
    private JTextField txtPlottitle;
    protected JTextField txtXtitle;
    private JTextField txtYtitle;
    private JComboBox<String> comboTheme;
    private JCheckBox chckbxShowLegend;
    private JTextField txtXlegend;
    private JCheckBox chckbxAutopos;
    private JCheckBox chckbxOpaque;
    private JTextField txtYlegend;
    private JLabel lblPosxy;
    
    /**
     * Create the panel.
     */
    public FEpanel_general() {
        initGUI();
    }
    
    public void setFrontend(Plot1DFrontend fe) {
        this.fe=fe;
        graphPanel = fe.getGraphPanel();
    }

    private void initGUI() {
      this.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "General", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      this.setLayout(new MigLayout("insets 2", "[][grow][grow][]", "[][][][][][]"));
      
      lblTitle = new JLabel("Title");
      this.add(lblTitle, "cell 0 0,alignx trailing");
      
      txtPlottitle = new JTextField();
      txtPlottitle.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              do_txtPlottitle_actionPerformed(e);
          }
      });
      this.add(txtPlottitle, "cell 1 0 3 1,growx");
      txtPlottitle.setColumns(10);
      
      lblXTitle = new JLabel("X title");
      this.add(lblXTitle, "cell 0 1,alignx trailing");
      
      txtXtitle = new JTextField();
      txtXtitle.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              do_txtXtitle_actionPerformed(e);
          }
      });
      txtXtitle.setText("xtitle");
      this.add(txtXtitle, "cell 1 1 3 1,growx");
      txtXtitle.setColumns(5);
      
      lblYTitle = new JLabel("Y title");
      this.add(lblYTitle, "cell 0 2,alignx trailing");
      
      txtYtitle = new JTextField();
      txtYtitle.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              do_txtYtitle_actionPerformed(e);
          }
      });
      txtYtitle.setText("ytitle");
      this.add(txtYtitle, "cell 1 2 3 1,growx");
      txtYtitle.setColumns(5);
      
      lblTheme = new JLabel("Theme");
      this.add(lblTheme, "cell 0 3,alignx trailing");
      
      comboTheme = new JComboBox<String>();
      comboTheme.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
              do_comboTheme_itemStateChanged(e);
          }
      });
      comboTheme.setModel(new DefaultComboBoxModel<String>(new String[] {"Light", "Dark"}));
      this.add(comboTheme, "cell 1 3 3 1,growx");
        
        chckbxShowLegend = new JCheckBox("Show legend");
        chckbxShowLegend.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxShow_itemStateChanged(e);
            }
        });
        this.add(chckbxShowLegend, "cell 0 4 2 1,alignx left");
        
        chckbxOpaque = new JCheckBox("Opaque");
        chckbxOpaque.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxOpaque_itemStateChanged(e);
            }
        });
        this.add(chckbxOpaque, "cell 2 4 2 1");
        
        lblPosxy = new JLabel("Pos (x,y)");
        add(lblPosxy, "cell 0 5,alignx trailing");
        
        txtXlegend = new JTextField();
        txtXlegend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXlegend_actionPerformed(e);
            }
        });
        txtXlegend.setText("Xlegend");
        this.add(txtXlegend, "cell 1 5,growx");
        txtXlegend.setColumns(2);
        
        txtYlegend = new JTextField();
        txtYlegend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYlegend_actionPerformed(e);
            }
        });
        txtYlegend.setText("Ylegend");
        this.add(txtYlegend, "cell 2 5,growx");
        txtYlegend.setColumns(2);
        
        chckbxAutopos = new JCheckBox("Auto");
        chckbxAutopos.setToolTipText("Automatic Position");
        chckbxAutopos.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxAutopos_itemStateChanged(e);
            }
        });
        this.add(chckbxAutopos, "cell 3 5,alignx left");
    }
    
  public void inicia() {
      if (graphPanel.isLightTheme()) {
          comboTheme.setSelectedIndex(0);
      }else {
          comboTheme.setSelectedIndex(1);
      }
      this.txtXtitle.setText(graphPanel.getXlabel());
      this.txtYtitle.setText(graphPanel.getYlabel());
      this.txtPlottitle.setText(graphPanel.getPlotTitle());
      this.chckbxShowLegend.setSelected(graphPanel.isShowLegend());
      this.chckbxAutopos.setSelected(graphPanel.isAutoPosLegend());
      this.txtXlegend.setText(Integer.toString(graphPanel.getLegendX())); //TODO mirar que passa quan hi ha canvi (que es crida i com actua)
      this.txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
      this.chckbxShowLegend.setSelected(graphPanel.isShowLegend());
      this.chckbxOpaque.setSelected(graphPanel.isLegend_opaque());
  }

  protected void do_txtXtitle_actionPerformed(ActionEvent e) {
      if (txtXtitle.getText().isEmpty()){
          graphPanel.setCustomXtitle(false);
      }else{
          graphPanel.setXlabel(txtXtitle.getText());
          graphPanel.setCustomXtitle(true);
      }   
      graphPanel.actualitzaPlot();
  }
  
  private void do_txtYtitle_actionPerformed(ActionEvent e) {
      graphPanel.setYlabel(txtYtitle.getText());
      graphPanel.actualitzaPlot();
  }
  

  
  private void do_txtPlottitle_actionPerformed(ActionEvent e) {
      graphPanel.setPlotTitle(txtPlottitle.getText());
      graphPanel.actualitzaPlot();
  }
  
  private void do_comboTheme_itemStateChanged(ItemEvent e) {
      if(e.getStateChange() == ItemEvent.DESELECTED)return;
      if (comboTheme.getSelectedItem().toString().equalsIgnoreCase("Light")){
          graphPanel.setLightTheme(true);
      }else{
          graphPanel.setLightTheme(false);
      }

      if (graphPanel.arePlottables()){
          boolean repaint = FileUtils.YesNoDialog(null, "Repaint current patterns?");
          if(repaint){
              this.reAssignColorToAllPaintedTypes();
          }
      }
  }
  
  private void do_chckbxShow_itemStateChanged(ItemEvent e) {
      graphPanel.setShowLegend(chckbxShowLegend.isSelected());
      graphPanel.actualitzaPlot();
      
  }
  private void do_chckbxAutopos_itemStateChanged(ItemEvent e) {
      graphPanel.setAutoPosLegend(chckbxAutopos.isSelected());
      if (chckbxAutopos.isSelected()){
          txtXlegend.setEditable(false);
          txtYlegend.setEditable(false);
      }else{
          txtXlegend.setEditable(true);
          txtYlegend.setEditable(true);
      }
      //legend pos Actualitzem sempre
      txtXlegend.setText(Integer.toString(graphPanel.getLegendX()));
      txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
      graphPanel.actualitzaPlot();
      
  }
  private void do_txtXlegend_actionPerformed(ActionEvent e) { //TODO pot haver-hi un bucle infinit?? revisar posant un log.debug()
      try{
          int lx = Integer.parseInt(txtXlegend.getText());
          graphPanel.setLegendX(lx);
          txtXlegend.setText(Integer.toString(graphPanel.getLegendX()));
          graphPanel.actualitzaPlot();
      }catch(Exception ex){
          fe.getLog().warning("Error reading legend X position");
      }
  }
  private void do_txtYlegend_actionPerformed(ActionEvent e) {
      try{
          int ly = Integer.parseInt(txtYlegend.getText());
          graphPanel.setLegendY(ly);
          txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
          graphPanel.actualitzaPlot();
      }catch(Exception ex){
          fe.getLog().warning("Error reading legend Y position");
      }
  }
  private void do_chckbxOpaque_itemStateChanged(ItemEvent e) {
      graphPanel.setLegend_opaque(chckbxOpaque.isSelected());
      graphPanel.actualitzaPlot();
  }
  
  public void reAssignColorToAllPaintedTypes() {
      graphPanel.getDataToPlot().reAssignColorPlottables(true, BasicSerie.getPaintedTypes().toArray(new SerieType[0]));    
  }

  public void updateXtitleFromGraphPanel() {
      this.txtXtitle.setText(graphPanel.getXlabel());
  }
  public void updateLegendPosFromGraphPanel() {
      this.txtXlegend.setText(Integer.toString(graphPanel.getLegendX()));
      this.txtYlegend.setText(Integer.toString(graphPanel.getLegendY()));
  }
    
    
  
  public void readOptions(Options opt) {
      boolean light = true;
      if(this.comboTheme.getSelectedIndex()==1)light=false;
      light = opt.getValAsBoolean("theme", light);
      if (light) {
          this.comboTheme.setSelectedIndex(0);
      }else {
          this.comboTheme.setSelectedIndex(1);
      }
      
      String xlabel = opt.getValAsString("xLabel", graphPanel.getXlabel());
      String ylabel = opt.getValAsString("yLabel", graphPanel.getYlabel());
      String plotTitle = opt.getValAsString("plotTitle", graphPanel.getPlotTitle());
      graphPanel.setXlabel(xlabel);
      graphPanel.setYlabel(ylabel);
      graphPanel.setPlotTitle(plotTitle);
      this.txtXtitle.setText(xlabel);
      this.txtYtitle.setText(ylabel);
      this.txtPlottitle.setText(plotTitle);
      
      if (!graphPanel.isAutoPosLegend()) {
          this.txtXlegend.setText(opt.getValAsString("legX", this.txtXlegend.getText()));
          this.txtYlegend.setText(opt.getValAsString("legY", this.txtYlegend.getText()));
          this.do_txtXlegend_actionPerformed(null);
          this.do_txtYlegend_actionPerformed(null);
        }
        this.chckbxShowLegend.setSelected(opt.getValAsBoolean("legend", this.chckbxShowLegend.isSelected()));
        this.chckbxAutopos.setSelected(opt.getValAsBoolean("autoLeg", this.chckbxAutopos.isSelected()));
        this.chckbxOpaque.setSelected(opt.getValAsBoolean("legBkg", this.chckbxOpaque.isSelected()));
  }
  
}
