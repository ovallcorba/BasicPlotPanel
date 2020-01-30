package com.vava33.BasicPlotPanel.frontend;

/**
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.vava33.BasicPlotPanel.core.Plot1DFrontend;
import com.vava33.BasicPlotPanel.core.Plot1DPanel;
import com.vava33.jutils.FileUtils;
import com.vava33.jutils.Options;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;

public class FEpanel_XYwindow extends JPanel {

    private static final long serialVersionUID = 1L;
    Plot1DFrontend fe;
    public Plot1DPanel<?> graphPanel;

    private JTextField txtXminwin;
    private JTextField txtXmaxwin;
    private JTextField txtYminwin;
    private JTextField txtYmaxwin;
    private JLabel lblXmin;
    private JLabel lblYmin_1;
    private JLabel lblGrid;
    private JCheckBox chckbxX;
    private JCheckBox chckbxY;
    private JCheckBox chckbxZoneScale;
    private JLabel lblTIni;
    private JLabel lblFactor;
    private JTextField txtZonescalexini;
    private JTextField txtZonescalefactor;
    private JLabel lblXmax;
    private JLabel lblYmax;
    
    public FEpanel_XYwindow() {
        initGUI();
    }
    
    public void setFrontend(Plot1DFrontend fe) {
        this.fe=fe;
        graphPanel = fe.getGraphPanel();
    }

    private void initGUI() {

        this.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "X,Y window", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        this.setLayout(new MigLayout("insets 2", "[][grow][][grow]", "[][][][][]"));
        
        txtXminwin = new JTextField();
        txtXminwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXminwin_actionPerformed(e);
            }
        });
        
        lblXmin = new JLabel("Xmin");
        this.add(lblXmin, "cell 0 0,alignx trailing");
        this.add(txtXminwin, "cell 1 0,growx");
        txtXminwin.setColumns(5);
        
        txtXmaxwin = new JTextField();
        txtXmaxwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtXmaxwin_actionPerformed(e);
            }
        });
        
        lblXmax = new JLabel("Xmax");
        add(lblXmax, "cell 2 0,alignx trailing");
        this.add(txtXmaxwin, "cell 3 0,growx");
        txtXmaxwin.setColumns(5);
        
        lblYmin_1 = new JLabel("Ymin");
        this.add(lblYmin_1, "cell 0 1,alignx trailing");
        
        txtYminwin = new JTextField();
        txtYminwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYminwin_actionPerformed(e);
            }
        });
        this.add(txtYminwin, "cell 1 1,growx");
        txtYminwin.setColumns(5);
        
        txtYmaxwin = new JTextField();
        txtYmaxwin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                do_txtYmaxwin_actionPerformed(e);
            }
        });
        
        lblYmax = new JLabel("Ymax");
        add(lblYmax, "cell 2 1,alignx trailing");
        this.add(txtYmaxwin, "cell 3 1,growx");
        txtYmaxwin.setColumns(5);
        
        lblGrid = new JLabel("Grid");
        this.add(lblGrid, "cell 0 2,alignx trailing");
        
        chckbxX = new JCheckBox("X");
        chckbxX.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxX_itemStateChanged(e);
            }
        });
        this.add(chckbxX, "flowx,cell 1 2,alignx center");
        
        
        chckbxZoneScale = new JCheckBox("Scale Y in region:");
        chckbxZoneScale.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                do_chckbxApply_itemStateChanged(e);
            }
        });
        
            
                
                chckbxY = new JCheckBox("Y");
                chckbxY.addItemListener(new ItemListener() {
                    public void itemStateChanged(ItemEvent e) {
                        do_chckbxY_itemStateChanged(e);
                    }
                });
                this.add(chckbxY, "cell 2 2,alignx left");
        this.add(chckbxZoneScale, "cell 0 3 4 1,alignx left");
        
        lblTIni = new JLabel("Xini");
        this.add(lblTIni, "cell 0 4,alignx trailing");
        
        txtZonescalexini = new JTextField();
        txtZonescalexini.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                do_txtZonescalexini_keyReleased(e);
            }
        });
        txtZonescalexini.setText("zoneScaleXini");
        this.add(txtZonescalexini, "cell 1 4,growx");
        txtZonescalexini.setColumns(5);
        
        lblFactor = new JLabel("Fact Y");
        this.add(lblFactor, "cell 2 4,alignx trailing");
        
        txtZonescalefactor = new JTextField();
        txtZonescalefactor.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                do_txtZonescalefactor_keyReleased(e);
            }
        });
        txtZonescalefactor.setText("zoneScaleFactor");
        this.add(txtZonescalefactor, "cell 3 4,growx");
        txtZonescalefactor.setColumns(5);
    }

    public void inicia() {
        this.chckbxX.setSelected(graphPanel.isShowGridX());
        this.chckbxY.setSelected(graphPanel.isShowGridY());
        this.txtZonescalexini.setText(FileUtils.dfX_2.format(graphPanel.getScaleFactorT2ang()));
        this.txtZonescalefactor.setText(FileUtils.dfX_2.format(graphPanel.getScaleFactorT2fact()));
    }
    
    public void applyWindow(){
        double xrangeMin=Double.parseDouble(txtXminwin.getText());
        double xrangeMax=Double.parseDouble(txtXmaxwin.getText());
        double yrangeMin=Double.parseDouble(txtYminwin.getText());
        double yrangeMax=Double.parseDouble(txtYmaxwin.getText());
        graphPanel.applyWindowLimits(xrangeMin,xrangeMax,yrangeMin,yrangeMax);

    }
    
    public void fillWindowValues(){
        double[] minmaxXYrange = graphPanel.getWindowValues();
        this.txtXminwin.setText(FileUtils.dfX_3.format(minmaxXYrange[0]));
        this.txtXmaxwin.setText(FileUtils.dfX_3.format(minmaxXYrange[1]));
        this.txtYminwin.setText(FileUtils.dfX_3.format(minmaxXYrange[2]));
        this.txtYmaxwin.setText(FileUtils.dfX_3.format(minmaxXYrange[3]));
    }
    
    
  private void do_chckbxApply_itemStateChanged(ItemEvent e) {
      applyPartialYscale();

  }
  private void do_txtZonescalexini_keyReleased(KeyEvent e) {
      if (e.getKeyCode()==KeyEvent.VK_ENTER) {
          applyPartialYscale();
      }
  }
  private void do_txtZonescalefactor_keyReleased(KeyEvent e) {
      if (e.getKeyCode()==KeyEvent.VK_ENTER) {
          applyPartialYscale();
      }
  }
  
  private void applyPartialYscale() {
      //default values
      float t2ini=30;
      float t2fac=1.0f;
      try {
          t2ini=Float.parseFloat(txtZonescalexini.getText());
          t2fac=Float.parseFloat(txtZonescalefactor.getText());
      }catch(Exception ex) {
          fe.getLog().warning("Error reading t2 value or factor");
          txtZonescalexini.setText("30.0");
          txtZonescalefactor.setText("1.0");
      }
      graphPanel.setPartialYScale(chckbxZoneScale.isSelected(),t2ini,t2fac);
      graphPanel.actualitzaPlot();
  }

    private void do_chckbxX_itemStateChanged(ItemEvent e) {
        graphPanel.setShowGridX(chckbxX.isSelected());
        graphPanel.actualitzaPlot();
    }
    private void do_chckbxY_itemStateChanged(ItemEvent e) {
        graphPanel.setShowGridY(chckbxY.isSelected());
        graphPanel.actualitzaPlot();
    }

    private void do_txtXminwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    protected void do_txtYminwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    private void do_txtXmaxwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    private void do_txtYmaxwin_actionPerformed(ActionEvent e) {
        this.applyWindow();
    }
    
    public void readOptions(Options opt) {
        //TODO si no es vol (o peta) fent servir els chckbox per default, millor agafar els de graphpanel
        this.chckbxX.setSelected(opt.getValAsBoolean("gridX", this.chckbxX.isSelected()));
        this.chckbxY.setSelected(opt.getValAsBoolean("gridY", this.chckbxY.isSelected()));
        this.txtZonescalexini.setText(opt.getValAsString("zoneScaleIni", txtZonescalexini.getText()));
        this.txtZonescalefactor.setText(opt.getValAsString("zoneScaleFac", txtZonescalefactor.getText()));
        this.chckbxZoneScale.setSelected(opt.getValAsBoolean("zoneScale", this.chckbxZoneScale.isSelected()));
        
        double[] ranges = graphPanel.getWindowValues();
        double xrangeMin=opt.getValAsDouble("xRangeMin",ranges[0]);
        double xrangeMax=opt.getValAsDouble("xRangeMax",ranges[1]);
        double yrangeMin=opt.getValAsDouble("yRangeMin",ranges[2]);
        double yrangeMax=opt.getValAsDouble("yRangeMax",ranges[3]);
        graphPanel.applyWindowLimits(xrangeMin,xrangeMax,yrangeMin,yrangeMax);
        double scalefitX=opt.getValAsDouble("scaleX",graphPanel.getScaleFitXY()[0]);
        double scalefitY=opt.getValAsDouble("scaleY",graphPanel.getScaleFitXY()[1]);
        graphPanel.setScaleFitXY(scalefitX,scalefitY);


    }
   
}